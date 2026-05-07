CREATE TABLE insumos (
    id                BIGSERIAL       PRIMARY KEY,
    nome              VARCHAR(150)    NOT NULL,
    descricao         TEXT,
    unidade_medida    VARCHAR(20)     NOT NULL,
    saldo_disponivel  NUMERIC(12, 4)  NOT NULL DEFAULT 0,
    estoque_minimo    NUMERIC(12, 4)  NOT NULL DEFAULT 0,
    created_at        TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ
);
