package com.producao.security;

import com.producao.domain.usuario.model.PerfilAcesso;
import com.producao.domain.usuario.model.Usuario;
import com.producao.infra.exception.RegraDeNegocioException;
import com.producao.security.service.ServicoJwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ServicoJwtTest {

    private static final String SEGREDO = "chave-secreta-para-testes-unitarios-com-tamanho-suficiente";
    private static final long EXPIRACAO_24H = 86_400_000L;
    private static final long EXPIRACAO_PASSADA = -1000L;

    private ServicoJwt servicoJwt;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        servicoJwt = new ServicoJwt(SEGREDO, EXPIRACAO_24H);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Operador Teste");
        usuario.setLogin("operador.teste");
        usuario.setSenha("$2a$10$hashedpassword");
        usuario.setPerfilAcesso(PerfilAcesso.OPERADOR);
        usuario.setAtivo(true);
    }

    @Test
    void gerarToken_deveRetornarTokenNaoVazio() {
        String token = servicoJwt.gerarToken(usuario);

        assertThat(token).isNotBlank();
    }

    @Test
    void extrairLogin_deveRetornarLoginCorreto() {
        String token = servicoJwt.gerarToken(usuario);

        String loginExtraido = servicoJwt.extrairLogin(token);

        assertThat(loginExtraido).isEqualTo("operador.teste");
    }

    @Test
    void tokenValido_deveRetornarTrueParaTokenRecente() {
        String token = servicoJwt.gerarToken(usuario);

        boolean valido = servicoJwt.tokenValido(token, usuario);

        assertThat(valido).isTrue();
    }

    @Test
    void tokenValido_deveRetornarFalseParaTokenExpirado() {
        ServicoJwt servicoComExpiracaoPassada = new ServicoJwt(SEGREDO, EXPIRACAO_PASSADA);
        String tokenExpirado = servicoComExpiracaoPassada.gerarToken(usuario);

        assertThatThrownBy(() -> servicoJwt.tokenValido(tokenExpirado, usuario))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Token expirado");
    }

    @Test
    void gerarToken_deveCriarTokenComPerfilNosClaims() {
        Usuario gestor = new Usuario();
        gestor.setId(2L);
        gestor.setLogin("gestor.teste");
        gestor.setSenha("$2a$10$hashedpassword");
        gestor.setPerfilAcesso(PerfilAcesso.GESTOR);
        gestor.setAtivo(true);

        String token = servicoJwt.gerarToken(gestor);

        assertThat(token).isNotBlank();
        assertThat(servicoJwt.extrairLogin(token)).isEqualTo("gestor.teste");
    }

    @Test
    void tokenValido_deveRetornarFalseParaUsuarioDiferente() {
        String token = servicoJwt.gerarToken(usuario);

        Usuario outrousuario = new Usuario();
        outrousuario.setLogin("outro.usuario");
        outrousuario.setSenha("senha");
        outrousuario.setPerfilAcesso(PerfilAcesso.OPERADOR);
        outrousuario.setAtivo(true);

        boolean valido = servicoJwt.tokenValido(token, outrousuario);

        assertThat(valido).isFalse();
    }
}
