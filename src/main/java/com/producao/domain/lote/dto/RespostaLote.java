package com.producao.domain.lote.dto;

import com.producao.domain.lote.model.Lote;
import com.producao.domain.lote.model.StatusLote;

import java.math.BigDecimal;
import java.time.Instant;

public record RespostaLote(
        Long id,
        String numeroLote,
        Long ordemProducaoId,
        String codigoOrdem,
        StatusLote status,
        Long insumoId,
        String nomeInsumo,
        BigDecimal quantidadeConsumida,
        String registradoPorLogin,
        Instant registradoEm
) {

    public static RespostaLote de(Lote lote) {
        return new RespostaLote(
                lote.getId(),
                lote.getNumeroLote(),
                lote.getOrdemProducao().getId(),
                lote.getOrdemProducao().getCodigoOrdem(),
                lote.getStatus(),
                lote.getInsumo().getId(),
                lote.getInsumo().getNome(),
                lote.getQuantidadeConsumida(),
                lote.getRegistradoPor().getLogin(),
                lote.getRegistradoEm()
        );
    }
}
