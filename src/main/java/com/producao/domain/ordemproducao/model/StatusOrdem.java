package com.producao.domain.ordemproducao.model;

/**
 * Status possiveis de uma ordem de producao.
 */
public enum StatusOrdem {

    /** Ordem criada, aguardando inicio */
    PENDENTE,

    /** Ordem em execucao */
    EM_ANDAMENTO,

    /** Ordem temporariamente pausada */
    PAUSADA,

    /** Ordem finalizada com sucesso */
    CONCLUIDA,

    /** Ordem cancelada */
    CANCELADA
}
