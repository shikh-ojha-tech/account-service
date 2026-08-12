ALTER TABLE account_transaction
    ADD COLUMN ledger_seq BIGSERIAL NOT NULL;

CREATE INDEX idx_transaction_account_ledger_seq
    ON account_transaction (account_id, ledger_seq DESC);

DROP INDEX IF EXISTS idx_transaction_account_created_at;
