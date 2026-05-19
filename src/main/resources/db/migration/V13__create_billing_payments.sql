CREATE TABLE billing_payments (
    id UUID PRIMARY KEY,
    billing_cycle_id UUID NOT NULL,
    account_id UUID NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_billing_cycle FOREIGN KEY (billing_cycle_id) REFERENCES billing_cycles(id),
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);