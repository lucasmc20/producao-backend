package com.producao.security.model;

import com.producao.domain.usuario.model.PerfilAcesso;

public record RespostaAutenticacao(
        Long id,
        String token,
        String login,
        String nome,
        PerfilAcesso perfil,
        long expiracaoMs
) {
}
