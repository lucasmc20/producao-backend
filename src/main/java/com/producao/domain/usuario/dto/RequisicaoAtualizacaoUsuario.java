package com.producao.domain.usuario.dto;

import com.producao.domain.usuario.model.PerfilAcesso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequisicaoAtualizacaoUsuario(

        @NotBlank(message = "Nome e obrigatorio")
        @Size(max = 150, message = "Nome deve ter no maximo 150 caracteres")
        String nome,

        @NotNull(message = "Perfil de acesso e obrigatorio")
        PerfilAcesso perfilAcesso,

        @Size(min = 6, message = "Senha deve ter no minimo 6 caracteres")
        String novaSenha
) {}
