CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    vpa VARCHAR(128) UNIQUE,
    kind VARCHAR(16) NOT NULL,
    balance_paise BIGINT NOT NULL DEFAULT 0,
    label VARCHAR(256) NOT NULL
);

CREATE TABLE ledger_lines (
    id UUID PRIMARY KEY,
    npci_txn_id VARCHAR(64) NOT NULL,
    account_id UUID NOT NULL REFERENCES accounts(id),
    debit_paise BIGINT NOT NULL DEFAULT 0,
    credit_paise BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE idempotency_records (
    idempotency_key VARCHAR(128) PRIMARY KEY,
    npci_txn_id VARCHAR(64) NOT NULL,
    response_status VARCHAR(32) NOT NULL,
    response_code VARCHAR(8),
    response_message VARCHAR(512)
);

CREATE INDEX idx_ledger_npci ON ledger_lines(npci_txn_id);
