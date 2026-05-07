CREATE TABLE maquinas (
    id            BIGSERIAL       PRIMARY KEY,
    nome          VARCHAR(100)    NOT NULL,
    tipo          VARCHAR(30)     NOT NULL,
    status        VARCHAR(30)     NOT NULL DEFAULT 'INATIVA',
    localizacao   VARCHAR(200),
    created_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ
);
