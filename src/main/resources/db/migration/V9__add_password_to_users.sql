-- Adiciona a coluna de senha na tabela de usuários. 
-- Colocamos um valor padrão temporário para não quebrar os usuários que já existem no banco.
ALTER TABLE users ADD COLUMN password VARCHAR(255) NOT NULL DEFAULT 'senha_temporaria_123';