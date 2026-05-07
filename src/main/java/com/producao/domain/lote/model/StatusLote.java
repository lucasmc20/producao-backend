package com.producao.domain.lote.model;

/**
 * Status possiveis de um lote de producao.
 */
public enum StatusLote {

    /** Lote aberto para registros */
    ABERTO,

    /** Lote fechado e finalizado */
    FECHADO,

    /** Lote cancelado */
    CANCELADO
}
