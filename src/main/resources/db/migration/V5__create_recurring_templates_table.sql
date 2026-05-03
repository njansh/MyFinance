CREATE TABLE recurring_templates (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    account_id UUID NOT NULL,
    category_id UUID,
    description VARCHAR(255) NOT NULL,
    expected_amount NUMERIC(19, 4) NOT NULL,
    type VARCHAR(20) NOT NULL, -- INCOME ou EXPENSE
    frequency_day INT NOT NULL, -- Dia sugerido (1 a 28)
    active BOOLEAN DEFAULT TRUE,
    last_executed_month INT, -- Mês de competência pago (1-12)
    last_executed_year INT,
    CONSTRAINT fk_template_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_template_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);