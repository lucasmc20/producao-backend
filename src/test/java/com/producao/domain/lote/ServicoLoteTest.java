package com.producao.domain.lote;

import com.producao.domain.insumo.model.Insumo;
import com.producao.domain.insumo.model.UnidadeMedida;
import com.producao.domain.insumo.service.ServicoInsumo;
import com.producao.domain.lote.dto.RequisicaoRegistroInsumo;
import com.producao.domain.lote.dto.RespostaLote;
import com.producao.domain.lote.model.Lote;
import com.producao.domain.lote.model.StatusLote;
import com.producao.domain.lote.repository.RepositorioLote;
import com.producao.domain.lote.service.ServicoLote;
import com.producao.domain.ordemproducao.model.OrdemProducao;
import com.producao.domain.ordemproducao.model.StatusOrdem;
import com.producao.domain.ordemproducao.repository.RepositorioOrdemProducao;
import com.producao.domain.usuario.model.PerfilAcesso;
import com.producao.domain.usuario.model.Usuario;
import com.producao.infra.exception.RegraDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoLoteTest {

    @Mock
    private RepositorioLote repositorioLote;

    @Mock
    private RepositorioOrdemProducao repositorioOrdemProducao;

    @Mock
    private ServicoInsumo servicoInsumo;

    @InjectMocks
    private ServicoLote servicoLote;

    private OrdemProducao ordemEmAndamento;
    private OrdemProducao ordemPendente;
    private Insumo insumo;
    private Usuario operador;
    private RequisicaoRegistroInsumo requisicao;

    @BeforeEach
    void setUp() {
        insumo = new Insumo();
        insumo.setId(10L);
        insumo.setNome("Álcool Etílico");
        insumo.setUnidadeMedida(UnidadeMedida.KG);
        insumo.setSaldoDisponivel(new BigDecimal("100.0000"));

        operador = new Usuario();
        operador.setId(1L);
        operador.setLogin("operador1");
        operador.setSenha("senha");
        operador.setPerfilAcesso(PerfilAcesso.OPERADOR);
        operador.setAtivo(true);

        ordemEmAndamento = new OrdemProducao();
        ordemEmAndamento.setId(100L);
        ordemEmAndamento.setCodigoOrdem("OP-001");
        ordemEmAndamento.setStatus(StatusOrdem.EM_ANDAMENTO);
        ordemEmAndamento.setQuantidadePlanejada(new BigDecimal("500.0000"));
        ordemEmAndamento.setQuantidadeProduzida(BigDecimal.ZERO);

        ordemPendente = new OrdemProducao();
        ordemPendente.setId(200L);
        ordemPendente.setCodigoOrdem("OP-002");
        ordemPendente.setStatus(StatusOrdem.PENDENTE);
        ordemPendente.setQuantidadePlanejada(new BigDecimal("500.0000"));
        ordemPendente.setQuantidadeProduzida(BigDecimal.ZERO);

        requisicao = new RequisicaoRegistroInsumo(10L, "LOTE-2024-001", new BigDecimal("25.0000"));

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                operador, null, operador.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void registrarConsumo_deveCriarLoteEChamarRegistrarConsumoDoServicoInsumo() {
        when(repositorioOrdemProducao.findById(100L)).thenReturn(Optional.of(ordemEmAndamento));
        when(repositorioLote.existsByNumeroLote("LOTE-2024-001")).thenReturn(false);
        when(servicoInsumo.buscarEntidadePorId(10L)).thenReturn(insumo);
        doNothing().when(servicoInsumo).registrarConsumo(eq(10L), eq(new BigDecimal("25.0000")));

        Lote loteSalvo = new Lote();
        loteSalvo.setId(1L);
        loteSalvo.setNumeroLote("LOTE-2024-001");
        loteSalvo.setOrdemProducao(ordemEmAndamento);
        loteSalvo.setInsumo(insumo);
        loteSalvo.setQuantidadeConsumida(new BigDecimal("25.0000"));
        loteSalvo.setStatus(StatusLote.ABERTO);
        loteSalvo.setRegistradoPor(operador);
        when(repositorioLote.save(any(Lote.class))).thenReturn(loteSalvo);

        RespostaLote resposta = servicoLote.registrarConsumo(100L, requisicao);

        assertThat(resposta).isNotNull();
        assertThat(resposta.numeroLote()).isEqualTo("LOTE-2024-001");
        verify(servicoInsumo).registrarConsumo(10L, new BigDecimal("25.0000"));
        verify(repositorioLote).save(any(Lote.class));
    }

    @Test
    void registrarConsumo_deveLancarRegraDeNegocioException_quandoOrdemNaoEstiverEmAndamento() {
        when(repositorioOrdemProducao.findById(200L)).thenReturn(Optional.of(ordemPendente));

        assertThatThrownBy(() -> servicoLote.registrarConsumo(200L, requisicao))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("EM_ANDAMENTO");

        verify(servicoInsumo, never()).registrarConsumo(any(), any());
        verify(repositorioLote, never()).save(any());
    }

    @Test
    void registrarConsumo_deveLancarRegraDeNegocioException_quandoOrdemEstiverConcluida() {
        OrdemProducao ordemConcluida = new OrdemProducao();
        ordemConcluida.setId(300L);
        ordemConcluida.setStatus(StatusOrdem.CONCLUIDA);
        when(repositorioOrdemProducao.findById(300L)).thenReturn(Optional.of(ordemConcluida));

        assertThatThrownBy(() -> servicoLote.registrarConsumo(300L, requisicao))
                .isInstanceOf(RegraDeNegocioException.class);

        verify(servicoInsumo, never()).registrarConsumo(any(), any());
    }

    @Test
    void registrarConsumo_deveLancarRegraDeNegocioException_quandoNumeroLoteJaExistir() {
        when(repositorioOrdemProducao.findById(100L)).thenReturn(Optional.of(ordemEmAndamento));
        when(repositorioLote.existsByNumeroLote("LOTE-2024-001")).thenReturn(true);

        assertThatThrownBy(() -> servicoLote.registrarConsumo(100L, requisicao))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("LOTE-2024-001");

        verify(servicoInsumo, never()).registrarConsumo(any(), any());
    }
}
