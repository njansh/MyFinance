ALTER TABLE categories ADD COLUMN user_id UUID;
-- Para manter a integridade, você deve associar as categorias existentes a um usuário de teste (se houver dados em dev)
-- ou limpar a tabela antes de aplicar a restrição NOT NULL.
ALTER TABLE categories ALTER COLUMN user_id SET NOT NULL;