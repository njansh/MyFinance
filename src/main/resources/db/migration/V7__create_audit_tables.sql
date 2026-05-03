CREATE TABLE revinfo_custom (
    rev SERIAL PRIMARY KEY,
    revtstmp BIGINT,
    user_id VARCHAR(255)
);

CREATE TABLE accounts_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    user_id UUID,
    type VARCHAR(50),
    name VARCHAR(255),
    balance NUMERIC(19, 4),
    version BIGINT,
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_accounts_revinfo FOREIGN KEY (rev) REFERENCES revinfo_custom(rev)
);

CREATE TABLE categories_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    name VARCHAR(255),
    user_id UUID,
    color VARCHAR(7),
    type VARCHAR(50),
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_categories_revinfo FOREIGN KEY (rev) REFERENCES revinfo_custom(rev)
);

CREATE TABLE transactions_aud (
    transaction_id UUID NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    transferid UUID,
    account_balance_after NUMERIC(19, 4),
    description VARCHAR(255),
    amount NUMERIC(19, 4),
    date TIMESTAMP,
    type VARCHAR(50),
    account_id UUID,
    category_id UUID,
    is_transfer BOOLEAN,
    version BIGINT,
    PRIMARY KEY (transaction_id, rev),
    CONSTRAINT fk_transactions_revinfo FOREIGN KEY (rev) REFERENCES revinfo_custom(rev)
);