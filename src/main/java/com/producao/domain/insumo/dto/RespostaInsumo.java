package com.producao.domain.insumo.dto;

import com.producao.domain.insumo.model.Insumo;
import com.producao.domain.insumo.model.UnidadeMedida;

import java.math.BigDecimal;
import java.time.Instant;

public record RespostaInsumo(
        Long id,
        String nome,
        String descricao,
        UnidadeMedida unidadeMedida,
        BigDecimal saldoDisponivel,
        BigDecimal estoqueMinimo,
        Instant createdAt,
        Instant updatedAt
) {

    public static RespostaInsumo de(Insumo insumo) {
        return new RespostaInsumo(
                insumo.getId(),
                insumo.getNome(),
                insumo.getDescricao(),
                insumo.getUnidadeMedida(),
                insumo.getSaldoDisponivel(),
                insumo.getEstoqueMinimo(),
                insumo.getCreatedAt(),
                insumo.getUpdatedAt()
        );
    }
}
