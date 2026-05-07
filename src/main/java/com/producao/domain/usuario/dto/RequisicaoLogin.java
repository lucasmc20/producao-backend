package com.producao.domain.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record RequisicaoLogin(

        @NotBlank(message = "Login e obrigatorio")
        String login,

        @NotBlank(message = "Senha e obrigatoria")
        String senha
) {
}
