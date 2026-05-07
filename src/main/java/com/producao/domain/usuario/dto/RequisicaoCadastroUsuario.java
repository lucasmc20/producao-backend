package com.producao.domain.usuario.dto;

import com.producao.domain.usuario.model.PerfilAcesso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequisicaoCadastroUsuario(

        @NotBlank(message = "Nome e obrigatorio")
        @Size(max = 150, message = "Nome deve ter no maximo 150 caracteres")
        String nome,

        @NotBlank(message = "Login e obrigatorio")
        @Size(max = 100, message = "Login deve ter no maximo 100 caracteres")
        String login,

        @NotBlank(message = "Senha e obrigatoria")
        @Size(min = 6, message = "Senha deve ter no minimo 6 caracteres")
        String senha,

        @NotNull(message = "Perfil de acesso e obrigatorio")
        PerfilAcesso perfilAcesso
) {
}
