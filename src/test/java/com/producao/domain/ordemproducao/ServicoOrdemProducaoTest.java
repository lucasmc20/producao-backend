package com.producao.domain.ordemproducao;

import com.producao.domain.maquina.model.Maquina;
import com.producao.domain.maquina.model.StatusMaquina;
import com.producao.domain.maquina.repository.RepositorioMaquina;
import com.producao.domain.ordemproducao.dto.RequisicaoCriarOrdem;
import com.producao.domain.ordemproducao.dto.RequisicaoFinalizarLote;
import com.producao.domain.ordemproducao.dto.RequisicaoIniciarLote;
import com.producao.domain.ordemproducao.dto.RespostaOrdemProducao;
import com.producao.domain.ordemproducao.model.OrdemProducao;
import com.producao.domain.ordemproducao.model.StatusOrdem;
import com.producao.domain.ordemproducao.repository.RepositorioOrdemProducao;
import com.producao.domain.ordemproducao.service.ServicoOrdemProducao;
import com.producao.domain.usuario.model.PerfilAcesso;
import com.producao.domain.usuario.model.Usuario;
import com.producao.domain.usuario.repository.RepositorioUsuario;
import com.producao.infra.exception.RegraDeNegocioException;
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
class ServicoOrdemProducaoTest {

    @Mock
    private RepositorioOrdemProducao repositorioOrdemProducao;

    @Mock
    private RepositorioMaquina repositorioMaquina;

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @InjectMocks
    private ServicoOrdemProducao servicoOrdemProducao;

    private Maquina maquinaInativa;
    private Maquina maquinaOperando;
    private Usuario operador;
    private OrdemProducao ordemPendente;
    private OrdemProducao ordemEmAndamento;

    @BeforeEach
    void setUp() {
        maquinaInativa = new Maquina();
        maquinaInativa.setId(1L);
        maquinaInativa.setNome("Envase-01");
        maquinaInativa.setTipo("ENVASE");
        maquinaInativa.setStatus(StatusMaquina.INATIVA);

        maquinaOperando = new Maquina();
        maquinaOperando.setId(2L);
        maquinaOperando.setNome("Mistura-01");
        maquinaOperando.setTipo("MISTURA");
        maquinaOperando.setStatus(StatusMaquina.OPERANDO);

        operador = new Usuario();
        operador.setId(1L);
        operador.setLogin("operador1");
        operador.setSenha("senha");
        operador.setPerfilAcesso(PerfilAcesso.OPERADOR);
        operador.setAtivo(true);

        ordemPendente = new OrdemProducao();
        ordemPendente.setId(10L);
        ordemPendente.setCodigoOrdem("OP-PENDENTE");
        ordemPendente.setStatus(StatusOrdem.PENDENTE);
        ordemPendente.setMaquina(maquinaInativa);
        ordemPendente.setOperador(operador);
        ordemPendente.setQuantidadePlanejada(new BigDecimal("1000.0000"));
        ordemPendente.setQuantidadeProduzida(BigDecimal.ZERO);

        ordemEmAndamento = new OrdemProducao();
        ordemEmAndamento.setId(20L);
        ordemEmAndamento.setCodigoOrdem("OP-EM-ANDAMENTO");
        ordemEmAndamento.setStatus(StatusOrdem.EM_ANDAMENTO);
        ordemEmAndamento.setMaquina(maquinaInativa);
        ordemEmAndamento.setOperador(operador);
        ordemEmAndamento.setQuantidadePlanejada(new BigDecimal("1000.0000"));
        ordemEmAndamento.setQuantidadeProduzida(BigDecimal.ZERO);
    }

    @Test
    void iniciarLote_deveMudarStatusParaEmAndamento() {
        when(repositorioOrdemProducao.findById(10L)).thenReturn(Optional.of(ordemPendente));
        when(repositorioUsuario.findById(1L)).thenReturn(Optional.of(operador));
        when(repositorioMaquina.save(any(Maquina.class))).thenReturn(maquinaInativa);
        when(repositorioOrdemProducao.save(any(OrdemProducao.class))).thenAnswer(inv -> inv.getArgument(0));

        RequisicaoIniciarLote requisicao = new RequisicaoIniciarLote(1L);
        RespostaOrdemProducao resposta = servicoOrdemProducao.iniciarLote(10L, requisicao);

        assertThat(resposta.status()).isEqualTo(StatusOrdem.EM_ANDAMENTO);
        verify(repositorioOrdemProducao).save(any(OrdemProducao.class));
    }

    @Test
    void iniciarLote_deveLancarRegraDeNegocioException_quandoOrdemJaEmAndamento() {
        when(repositorioOrdemProducao.findById(20L)).thenReturn(Optional.of(ordemEmAndamento));

        RequisicaoIniciarLote requisicao = new RequisicaoIniciarLote(1L);

        assertThatThrownBy(() -> servicoOrdemProducao.iniciarLote(20L, requisicao))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("ja em andamento");

        verify(repositorioOrdemProducao, never()).save(any());
    }

    @Test
    void finalizarLote_deveMudarStatusParaConcluida() {
        when(repositorioOrdemProducao.findById(20L)).thenReturn(Optional.of(ordemEmAndamento));
        when(repositorioMaquina.save(any(Maquina.class))).thenReturn(maquinaInativa);
        when(repositorioOrdemProducao.save(any(OrdemProducao.class))).thenAnswer(inv -> inv.getArgument(0));

        RequisicaoFinalizarLote requisicao = new RequisicaoFinalizarLote(new BigDecimal("950.0000"));
        RespostaOrdemProducao resposta = servicoOrdemProducao.finalizarLote(20L, requisicao);

        assertThat(resposta.status()).isEqualTo(StatusOrdem.CONCLUIDA);
        assertThat(resposta.quantidadeProduzida()).isEqualByComparingTo(new BigDecimal("950.0000"));
    }

    @Test
    void finalizarLote_deveLancarRegraDeNegocioException_quandoOrdemNaoEstiverEmAndamento() {
        when(repositorioOrdemProducao.findById(10L)).thenReturn(Optional.of(ordemPendente));

        RequisicaoFinalizarLote requisicao = new RequisicaoFinalizarLote(new BigDecimal("100.0000"));

        assertThatThrownBy(() -> servicoOrdemProducao.finalizarLote(10L, requisicao))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("EM_ANDAMENTO");

        verify(repositorioOrdemProducao, never()).save(any());
    }

    @Test
    void criar_deveLancarRegraDeNegocioException_quandoMaquinaEstiverOperando() {
        when(repositorioMaquina.findById(2L)).thenReturn(Optional.of(maquinaOperando));

        RequisicaoCriarOrdem requisicao = new RequisicaoCriarOrdem(
                "Produção de sabonete",
                2L,
                1L,
                new BigDecimal("500.0000"),
                null
        );

        assertThatThrownBy(() -> servicoOrdemProducao.criar(requisicao))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("OPERANDO");

        verify(repositorioOrdemProducao, never()).save(any());
    }

    @Test
    void criar_deveSalvarOrdemQuandoMaquinaDisponivel() {
        when(repositorioMaquina.findById(1L)).thenReturn(Optional.of(maquinaInativa));
        when(repositorioUsuario.findById(1L)).thenReturn(Optional.of(operador));
        when(repositorioOrdemProducao.save(any(OrdemProducao.class))).thenAnswer(inv -> {
            OrdemProducao o = inv.getArgument(0);
            o.setId(99L);
            return o;
        });

        RequisicaoCriarOrdem requisicao = new RequisicaoCriarOrdem(
                "Produção de álcool",
                1L,
                1L,
                new BigDecimal("1000.0000"),
                "OP-TESTE"
        );

        RespostaOrdemProducao resposta = servicoOrdemProducao.criar(requisicao);

        assertThat(resposta.status()).isEqualTo(StatusOrdem.PENDENTE);
        assertThat(resposta.codigoOrdem()).isEqualTo("OP-TESTE");
        verify(repositorioOrdemProducao).save(any(OrdemProducao.class));
    }
}
