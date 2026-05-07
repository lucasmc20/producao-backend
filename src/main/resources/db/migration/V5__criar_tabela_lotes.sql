CREATE TABLE lotes (
    id                    BIGSERIAL       PRIMARY KEY,
    numero_lote           VARCHAR(50)     NOT NULL UNIQUE,
    ordem_producao_id     BIGINT          NOT NULL REFERENCES ordens_producao(id),
    status                VARCHAR(20)     NOT NULL DEFAULT 'ABERTO',
    insumo_id             BIGINT          NOT NULL REFERENCES insumos(id),
    quantidade_consumida  NUMERIC(12, 4)  NOT NULL,
    registrado_por        BIGINT          NOT NULL REFERENCES usuarios(id),
    registrado_em         TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_lotes_ordem ON lotes (ordem_producao_id);
