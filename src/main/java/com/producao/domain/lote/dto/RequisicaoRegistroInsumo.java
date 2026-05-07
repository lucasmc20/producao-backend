package com.producao.domain.lote.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RequisicaoRegistroInsumo(

        @NotNull(message = "ID do insumo e obrigatorio")
        Long insumoId,

        @NotBlank(message = "Numero do lote e obrigatorio")
        @Size(max = 50, message = "Numero do lote deve ter no maximo 50 caracteres")
        String numeroLote,

        @NotNull(message = "Quantidade consumida e obrigatoria")
        @DecimalMin(value = "0.0001", message = "Quantidade deve ser maior que zero")
        BigDecimal quantidadeConsumida
) {
}
