package com.producao.domain.maquina.dto;

import com.producao.domain.maquina.model.StatusMaquina;

import java.time.Instant;

public record EventoStatusMaquina(
        Long maquinaId,
        String nomeMaquina,
        StatusMaquina statusAnterior,
        StatusMaquina statusAtual,
        Instant ocorridoEm
) {
}
