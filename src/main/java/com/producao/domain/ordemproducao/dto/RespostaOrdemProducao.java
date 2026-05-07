package com.producao.domain.ordemproducao.dto;

import com.producao.domain.ordemproducao.model.OrdemProducao;
import com.producao.domain.ordemproducao.model.StatusOrdem;

import java.math.BigDecimal;
import java.time.Instant;

public record RespostaOrdemProducao(
        Long id,
        String codigoOrdem,
        String descricao,
        StatusOrdem status,
        Long maquinaId,
        String nomeMaquina,
        Long operadorId,
        String loginOperador,
        BigDecimal quantidadePlanejada,
        BigDecimal quantidadeProduzida,
        Instant iniciadoEm,
        Instant finalizadoEm,
        Instant createdAt,
        Instant updatedAt
) {

    public static RespostaOrdemProducao de(OrdemProducao ordem) {
        return new RespostaOrdemProducao(
                ordem.getId(),
                ordem.getCodigoOrdem(),
                ordem.getDescricao(),
                ordem.getStatus(),
                ordem.getMaquina() != null ? ordem.getMaquina().getId() : null,
                ordem.getMaquina() != null ? ordem.getMaquina().getNome() : null,
                ordem.getOperador() != null ? ordem.getOperador().getId() : null,
                ordem.getOperador() != null ? ordem.getOperador().getLogin() : null,
                ordem.getQuantidadePlanejada(),
                ordem.getQuantidadeProduzida(),
                ordem.getIniciadoEm(),
                ordem.getFinalizadoEm(),
                ordem.getCreatedAt(),
                ordem.getUpdatedAt()
        );
    }
}
