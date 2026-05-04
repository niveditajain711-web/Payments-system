CREATE TABLE vpa_directory (
    vpa VARCHAR(128) PRIMARY KEY,
    bank_code VARCHAR(8) NOT NULL,
    account_id UUID NOT NULL
);

CREATE TABLE audit_log (
    id UUID PRIMARY KEY,
    npci_txn_id VARCHAR(64) NOT NULL,
    correlation_id VARCHAR(64),
    operation VARCHAR(32) NOT NULL,
    detail VARCHAR(1024),
    response_code VARCHAR(8),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE collect_requests (
    collect_id UUID PRIMARY KEY,
    payer_vpa VARCHAR(128) NOT NULL,
    payee_vpa VARCHAR(128) NOT NULL,
    amount_paise BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL,
    npci_txn_id VARCHAR(64),
    note VARCHAR(512)
);

CREATE TABLE pay_idempotency (
    idempotency_key VARCHAR(128) PRIMARY KEY,
    npci_txn_id VARCHAR(64) NOT NULL,
    response_json TEXT NOT NULL
);
