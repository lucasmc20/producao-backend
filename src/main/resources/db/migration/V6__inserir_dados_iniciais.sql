-- =============================================================
-- USUARIOS DE TESTE (apenas para ambiente de desenvolvimento)
-- -------------------------------------------------------------
--   login      | senha        | perfil
-- -------------|--------------|-------------
--   admin      | admin123     | ADMINISTRADOR
--   gestor     | gestor123    | GESTOR
--   operador1  | operador123  | OPERADOR
--   operador2  | operador123  | OPERADOR
-- =============================================================
INSERT INTO usuarios (nome, login, senha, perfil_acesso) VALUES
('Administrador do Sistema', 'admin',     '$2b$10$I9rHMYTEaWYGUzKRaShlBeGqvYvtDYOsN.vzxH87IuJl6z4ThWFqm', 'ADMINISTRADOR'),
('Gestor de Producao',       'gestor',    '$2b$10$3RCBg1diA5HpYEDz.HjR7O0zDKXy0EC0wtqryycvw/5Vfgr5rAZUy', 'GESTOR'),
('Operador Um',              'operador1', '$2b$10$UfDOYyJtdHPYRBtRAIJ7/.X9KOJohmhXjOeixdKGGBaBHOUA5HmK6', 'OPERADOR'),
('Operador Dois',            'operador2', '$2b$10$8a4vmSGOzD3lR2beMmLO3OVZxcICo/ExD.sALx5ldfZHdd9hnNBQq', 'OPERADOR');

-- Insumos iniciais
INSERT INTO insumos (nome, descricao, unidade_medida, saldo_disponivel, estoque_minimo) VALUES
('Alcool Etilico', 'Alcool etilico 96% para formulacoes', 'KG', 500.0000, 50.0000),
('Agua Purificada', 'Agua purificada por osmose reversa', 'LITRO', 2000.0000, 200.0000),
('Embalagem PET', 'Embalagem PET 500ml', 'UNIDADE', 10000.0000, 1000.0000);

-- Maquinas iniciais
INSERT INTO maquinas (nome, tipo, status, localizacao) VALUES
('Envase-01', 'ENVASE', 'INATIVA', 'Galpao A - Linha 1'),
('Mistura-01', 'MISTURA', 'INATIVA', 'Galpao B - Linha 2'),
('Rotulagem-01', 'ROTULAGEM', 'INATIVA', 'Galpao A - Linha 3');
