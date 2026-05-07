package com.producao.domain.ordemproducao.dto;

import jakarta.validation.constraints.NotNull;

public record RequisicaoIniciarLote(

        @NotNull(message = "ID do operador e obrigatorio")
        Long operadorId
) {
}
