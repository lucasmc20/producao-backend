CREATE TABLE ordens_producao (
    id                    BIGSERIAL       PRIMARY KEY,
    codigo_ordem          VARCHAR(30)     NOT NULL UNIQUE,
    descricao             TEXT,
    status                VARCHAR(30)     NOT NULL DEFAULT 'PENDENTE',
    maquina_id            BIGINT          REFERENCES maquinas(id),
    operador_id           BIGINT          REFERENCES usuarios(id),
    quantidade_planejada  NUMERIC(12, 4)  NOT NULL,
    quantidade_produzida  NUMERIC(12, 4)  NOT NULL DEFAULT 0,
    iniciado_em           TIMESTAMPTZ,
    finalizado_em         TIMESTAMPTZ,
    created_at            TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ
);

CREATE INDEX idx_ordens_producao_status ON ordens_producao (status);
CREATE INDEX idx_ordens_producao_maquina ON ordens_producao (maquina_id);
