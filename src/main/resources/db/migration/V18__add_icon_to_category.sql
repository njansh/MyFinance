-- Adiciona a coluna na tabela principal
ALTER TABLE categories ADD COLUMN icon VARCHAR(50);
UPDATE categories SET icon = 'Circle' WHERE icon IS NULL;
ALTER TABLE categories ALTER COLUMN icon SET NOT NULL;

-- Adiciona a coluna na tabela de auditoria
ALTER TABLE categories_aud ADD COLUMN icon VARCHAR(50);