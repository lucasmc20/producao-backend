package com.producao.domain.ordemproducao.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RequisicaoFinalizarLote(

        @NotNull(message = "Quantidade produzida e obrigatoria")
        @DecimalMin(value = "0.0", inclusive = true, message = "Quantidade produzida nao pode ser negativa")
        BigDecimal quantidadeProduzida
) {
}
