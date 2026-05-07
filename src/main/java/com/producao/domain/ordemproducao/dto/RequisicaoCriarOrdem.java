package com.producao.domain.ordemproducao.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RequisicaoCriarOrdem(

        @NotBlank(message = "Descricao e obrigatoria")
        String descricao,

        @NotNull(message = "ID da maquina e obrigatorio")
        Long maquinaId,

        @NotNull(message = "ID do operador e obrigatorio")
        Long operadorId,

        @NotNull(message = "Quantidade planejada e obrigatoria")
        @DecimalMin(value = "0.0001", message = "Quantidade planejada deve ser maior que zero")
        BigDecimal quantidadePlanejada,

        @Size(max = 30, message = "Codigo da ordem deve ter no maximo 30 caracteres")
        String codigoOrdem
) {
}
