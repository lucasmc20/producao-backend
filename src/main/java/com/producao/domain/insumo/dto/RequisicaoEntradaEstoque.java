package com.producao.domain.insumo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RequisicaoEntradaEstoque(

        @NotNull(message = "Quantidade e obrigatoria")
        @DecimalMin(value = "0.0001", inclusive = true, message = "Quantidade deve ser maior que zero")
        BigDecimal quantidade,

        String observacao
) {}
