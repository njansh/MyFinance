CREATE TABLE idempotency_records (
                                     idempotency_key VARCHAR(255) PRIMARY KEY,
                                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);