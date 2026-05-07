package com.producao.domain.ordemproducao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.producao.config.SegurancaConfig;
import com.producao.domain.ordemproducao.controller.ControladorOrdemProducao;
import com.producao.domain.ordemproducao.dto.RequisicaoIniciarLote;
import com.producao.domain.ordemproducao.dto.RespostaOrdemProducao;
import com.producao.domain.ordemproducao.model.StatusOrdem;
import com.producao.domain.ordemproducao.service.ServicoOrdemProducao;
import com.producao.domain.usuario.model.PerfilAcesso;
import com.producao.domain.usuario.model.Usuario;
import com.producao.security.service.ServicoDetalhesUsuario;
import com.producao.security.service.ServicoJwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ControladorOrdemProducao.class)
@Import(SegurancaConfig.class)
class ControladorOrdemProducaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServicoOrdemProducao servicoOrdemProducao;

    @MockBean
    private ServicoJwt servicoJwt;

    @MockBean
    private ServicoDetalhesUsuario servicoDetalhesUsuario;

    private Usuario usuarioOperador;
    private Usuario usuarioGestor;
    private RespostaOrdemProducao ordemResposta;

    @BeforeEach
    void setUp() {
        usuarioOperador = new Usuario();
        usuarioOperador.setId(1L);
        usuarioOperador.setNome("Operador");
        usuarioOperador.setLogin("operador1");
        usuarioOperador.setSenha("senha");
        usuarioOperador.setPerfilAcesso(PerfilAcesso.OPERADOR);
        usuarioOperador.setAtivo(true);

        usuarioGestor = new Usuario();
        usuarioGestor.setId(2L);
        usuarioGestor.setNome("Gestor");
        usuarioGestor.setLogin("gestor1");
        usuarioGestor.setSenha("senha");
        usuarioGestor.setPerfilAcesso(PerfilAcesso.GESTOR);
        usuarioGestor.setAtivo(true);

        ordemResposta = new RespostaOrdemProducao(
                1L, "OP-001", "Produção teste",
                StatusOrdem.PENDENTE,
                1L, "Envase-01",
                1L, "operador1",
                new BigDecimal("100.0000"),
                BigDecimal.ZERO,
                null, null, null, null
        );
    }

    @Test
    void listarTodas_deveRetornar200_quandoAutenticado() throws Exception {
        when(servicoOrdemProducao.listarTodas()).thenReturn(List.of(ordemResposta));

        mockMvc.perform(get("/orders")
                        .with(user(usuarioOperador)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true));
    }

    @Test
    void listarTodas_deveRetornar401_quandoSemToken() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void criar_deveRetornar403_quandoPerfilForOperador() throws Exception {
        String corpo = objectMapper.writeValueAsString(
                new com.producao.domain.ordemproducao.dto.RequisicaoCriarOrdem(
                        "Produção de teste", 1L, 1L, new BigDecimal("100.0000"), null
                )
        );

        mockMvc.perform(post("/orders")
                        .with(user(usuarioOperador))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isForbidden());
    }

    @Test
    void iniciarLote_deveRetornar200EChamarService_quandoAutenticado() throws Exception {
        RespostaOrdemProducao respostaAtualizada = new RespostaOrdemProducao(
                1L, "OP-001", "Produção teste",
                StatusOrdem.EM_ANDAMENTO,
                1L, "Envase-01",
                1L, "operador1",
                new BigDecimal("100.0000"),
                BigDecimal.ZERO,
                null, null, null, null
        );
        when(servicoOrdemProducao.iniciarLote(eq(1L), any(RequisicaoIniciarLote.class)))
                .thenReturn(respostaAtualizada);

        String corpo = objectMapper.writeValueAsString(new RequisicaoIniciarLote(1L));

        mockMvc.perform(patch("/orders/1/iniciar")
                        .with(user(usuarioOperador))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true));

        verify(servicoOrdemProducao).iniciarLote(eq(1L), any(RequisicaoIniciarLote.class));
    }

    @Test
    void criar_deveRetornar201_quandoPerfilForGestor() throws Exception {
        when(servicoOrdemProducao.criar(any())).thenReturn(ordemResposta);

        String corpo = objectMapper.writeValueAsString(
                new com.producao.domain.ordemproducao.dto.RequisicaoCriarOrdem(
                        "Produção de teste", 1L, 1L, new BigDecimal("100.0000"), null
                )
        );

        mockMvc.perform(post("/orders")
                        .with(user(usuarioGestor))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sucesso").value(true));
    }
}
