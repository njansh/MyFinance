CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE accounts (
                          id UUID PRIMARY KEY,
                          user_id UUID NOT NULL,
                          type VARCHAR(50) NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          balance NUMERIC(19, 4) NOT NULL,
                          version BIGINT DEFAULT 0
);

CREATE TABLE categories (
                            id UUID PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            color VARCHAR(7) NOT NULL,
                            type VARCHAR(50) NOT NULL
);

CREATE TABLE transactions (
                              transaction_id UUID PRIMARY KEY,
                              transferid UUID,
                              account_balance_after NUMERIC(19, 4),
                              description VARCHAR(255) NOT NULL,
                              amount NUMERIC(19, 4) NOT NULL,
                              date TIMESTAMP NOT NULL,
                              type VARCHAR(50) NOT NULL,
                              account_id UUID NOT NULL,
                              category_id UUID,
                              is_transfer BOOLEAN NOT NULL,
                              version BIGINT DEFAULT 0
);