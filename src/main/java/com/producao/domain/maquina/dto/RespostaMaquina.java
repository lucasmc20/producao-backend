package com.producao.domain.maquina.dto;

import com.producao.domain.maquina.model.Maquina;
import com.producao.domain.maquina.model.StatusMaquina;

import java.time.Instant;

public record RespostaMaquina(
        Long id,
        String nome,
        String tipo,
        StatusMaquina status,
        String localizacao,
        Instant createdAt,
        Instant updatedAt
) {

    public static RespostaMaquina de(Maquina maquina) {
        return new RespostaMaquina(
                maquina.getId(),
                maquina.getNome(),
                maquina.getTipo(),
                maquina.getStatus(),
                maquina.getLocalizacao(),
                maquina.getCreatedAt(),
                maquina.getUpdatedAt()
        );
    }
}
