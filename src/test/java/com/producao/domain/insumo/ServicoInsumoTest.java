package com.producao.domain.insumo;

import com.producao.domain.insumo.dto.RespostaSaldoEstoque;
import com.producao.domain.insumo.model.Insumo;
import com.producao.domain.insumo.model.UnidadeMedida;
import com.producao.domain.insumo.repository.RepositorioInsumo;
import com.producao.domain.insumo.service.ServicoInsumo;
import com.producao.infra.exception.RecursoNaoEncontradoException;
import com.producao.infra.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoInsumoTest {

    @Mock
    private RepositorioInsumo repositorioInsumo;

    @InjectMocks
    private ServicoInsumo servicoInsumo;

    private Insumo insumo;

    @BeforeEach
    void setUp() {
        insumo = new Insumo();
        insumo.setId(1L);
        insumo.setNome("Álcool Etílico");
        insumo.setUnidadeMedida(UnidadeMedida.KG);
        insumo.setSaldoDisponivel(new BigDecimal("100.0000"));
        insumo.setEstoqueMinimo(new BigDecimal("10.0000"));
    }

    @Test
    void consultarSaldo_deveRetornarSaldoSuficienteTrue_quandoSaldoMaiorQueRequerido() {
        when(repositorioInsumo.findById(1L)).thenReturn(Optional.of(insumo));

        RespostaSaldoEstoque resposta = servicoInsumo.consultarSaldo(1L, new BigDecimal("50.0000"));

        assertThat(resposta.saldoSuficiente()).isTrue();
        assertThat(resposta.saldoDisponivel()).isEqualByComparingTo(new BigDecimal("100.0000"));
    }

    @Test
    void consultarSaldo_deveRetornarSaldoSuficienteFalse_quandoSaldoMenorQueRequerido() {
        when(repositorioInsumo.findById(1L)).thenReturn(Optional.of(insumo));

        RespostaSaldoEstoque resposta = servicoInsumo.consultarSaldo(1L, new BigDecimal("150.0000"));

        assertThat(resposta.saldoSuficiente()).isFalse();
    }

    @Test
    void consultarSaldo_deveRetornarSaldoSuficienteTrue_quandoSaldoIgualAoRequerido() {
        when(repositorioInsumo.findById(1L)).thenReturn(Optional.of(insumo));

        RespostaSaldoEstoque resposta = servicoInsumo.consultarSaldo(1L, new BigDecimal("100.0000"));

        assertThat(resposta.saldoSuficiente()).isTrue();
    }

    @Test
    void registrarConsumo_deveSubtrairSaldoDisponivel() {
        when(repositorioInsumo.findById(1L)).thenReturn(Optional.of(insumo));
        when(repositorioInsumo.save(any(Insumo.class))).thenReturn(insumo);

        servicoInsumo.registrarConsumo(1L, new BigDecimal("30.0000"));

        assertThat(insumo.getSaldoDisponivel()).isEqualByComparingTo(new BigDecimal("70.0000"));
        verify(repositorioInsumo).save(insumo);
    }

    @Test
    void registrarConsumo_deveLancarSaldoInsuficienteException_quandoSaldoInsuficiente() {
        when(repositorioInsumo.findById(1L)).thenReturn(Optional.of(insumo));

        assertThatThrownBy(() -> servicoInsumo.registrarConsumo(1L, new BigDecimal("200.0000")))
                .isInstanceOf(SaldoInsuficienteException.class)
                .hasMessageContaining("Saldo insuficiente");

        verify(repositorioInsumo, never()).save(any());
    }

    @Test
    void buscarPorId_deveLancarRecursoNaoEncontradoException_quandoNaoExistir() {
        when(repositorioInsumo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicoInsumo.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");
    }
}
