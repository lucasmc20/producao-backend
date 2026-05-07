package com.producao.domain.insumo.dto;

import com.producao.domain.insumo.model.Insumo;
import com.producao.domain.insumo.model.UnidadeMedida;

import java.math.BigDecimal;

public record RespostaSaldoEstoque(
        Long insumoId,
        String nomeInsumo,
        UnidadeMedida unidadeMedida,
        BigDecimal saldoDisponivel,
        BigDecimal estoqueMinimo,
        boolean saldoSuficiente
) {

    public static RespostaSaldoEstoque de(Insumo insumo, BigDecimal quantidadeRequerida) {
        boolean suficiente = insumo.getSaldoDisponivel().compareTo(quantidadeRequerida) >= 0;
        return new RespostaSaldoEstoque(
                insumo.getId(),
                insumo.getNome(),
                insumo.getUnidadeMedida(),
                insumo.getSaldoDisponivel(),
                insumo.getEstoqueMinimo(),
                suficiente
        );
    }
}
