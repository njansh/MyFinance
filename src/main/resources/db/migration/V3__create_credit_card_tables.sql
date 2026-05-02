CREATE TABLE credit_cards (
                              id UUID PRIMARY KEY,
                              account_id UUID NOT NULL,
                              name VARCHAR(255) NOT NULL,
                              credit_limit NUMERIC(19, 4) NOT NULL,
                              closing_day INT NOT NULL,
                              due_day INT NOT NULL,
                              CONSTRAINT fk_cc_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE billing_cycles (
                                id UUID PRIMARY KEY,
                                credit_card_id UUID NOT NULL,
                                start_date DATE NOT NULL,
                                closing_date DATE NOT NULL,
                                due_date DATE NOT NULL,
                                total_amount NUMERIC(19, 4) NOT NULL,
                                status VARCHAR(50) NOT NULL,
                                version BIGINT DEFAULT 0,
                                CONSTRAINT fk_bc_credit_card FOREIGN KEY (credit_card_id) REFERENCES credit_cards(id)
);