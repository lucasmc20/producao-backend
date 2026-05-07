package com.producao.domain.usuario.dto;

import com.producao.domain.usuario.model.PerfilAcesso;
import com.producao.domain.usuario.model.Usuario;

public record RespostaUsuario(
        Long id,
        String nome,
        String login,
        PerfilAcesso perfilAcesso,
        Boolean ativo
) {
    public static RespostaUsuario de(Usuario usuario) {
        return new RespostaUsuario(
                usuario.getId(),
                usuario.getNome(),
                usuario.getLogin(),
                usuario.getPerfilAcesso(),
                usuario.getAtivo()
        );
    }
}
