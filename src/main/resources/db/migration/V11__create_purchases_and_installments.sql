CREATE TABLE credit_card_purchases (
    id UUID PRIMARY KEY,
    credit_card_id UUID NOT NULL,
    description VARCHAR(255) NOT NULL,
    total_amount NUMERIC(19, 4) NOT NULL,
    total_installments INT NOT NULL,
    purchase_date DATE NOT NULL
);

CREATE TABLE credit_card_installments (
    id UUID PRIMARY KEY,
    purchase_id UUID NOT NULL,
    billing_cycle_id UUID NOT NULL,
    installment_number INT NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    status VARCHAR(50) NOT NULL
);