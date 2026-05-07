package com.producao.domain.maquina.model;

/**
 * Status possiveis de uma maquina no chao de fabrica.
 */
public enum StatusMaquina {

    /** Maquina desligada ou fora de operacao */
    INATIVA,

    /** Maquina em operacao normal */
    OPERANDO,

    /** Maquina em manutencao preventiva ou corretiva */
    MANUTENCAO,

    /** Maquina parada por emergencia */
    PARADA_EMERGENCIA
}
