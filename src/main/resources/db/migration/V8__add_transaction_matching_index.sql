-- Cria um indice composto para acelerar as operacoes de busca (account_id, date, amount)
CREATE INDEX IF NOT EXISTS idx_transaction_matching ON transactions (account_id, date, amount);