package com.producao.domain.insumo.dto;

import com.producao.domain.insumo.model.UnidadeMedida;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RequisicaoCadastroInsumo(

        @NotBlank(message = "Nome e obrigatorio")
        @Size(max = 150, message = "Nome deve ter no maximo 150 caracteres")
        String nome,

        String descricao,

        @NotNull(message = "Unidade de medida e obrigatoria")
        UnidadeMedida unidadeMedida,

        @NotNull(message = "Saldo inicial e obrigatorio")
        @DecimalMin(value = "0.0", message = "Saldo inicial nao pode ser negativo")
        BigDecimal saldoInicial,

        @NotNull(message = "Estoque minimo e obrigatorio")
        @DecimalMin(value = "0.0", message = "Estoque minimo nao pode ser negativo")
        BigDecimal estoqueMinimo
) {}
