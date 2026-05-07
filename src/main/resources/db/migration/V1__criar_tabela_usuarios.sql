CREATE TABLE usuarios (
    id             BIGSERIAL       PRIMARY KEY,
    nome           VARCHAR(150)    NOT NULL,
    login          VARCHAR(100)    NOT NULL UNIQUE,
    senha          VARCHAR(255)    NOT NULL,
    perfil_acesso  VARCHAR(20)     NOT NULL,
    ativo          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ
);

CREATE INDEX idx_usuarios_login ON usuarios (login);
