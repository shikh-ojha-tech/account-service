CREATE TABLE account (
    account_id   UUID         PRIMARY KEY,
    customer_id  VARCHAR(64)  NOT NULL,
    country      VARCHAR(2)   NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_account_customer_id ON account (customer_id);

CREATE TABLE balance (
    balance_id       UUID           PRIMARY KEY,
    account_id       UUID           NOT NULL REFERENCES account (account_id),
    currency         VARCHAR(3)     NOT NULL,
    available_amount NUMERIC(19, 2) NOT NULL DEFAULT 0,
    updated_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_balance_currency CHECK (currency IN ('EUR', 'SEK', 'GBP', 'USD')),
    CONSTRAINT chk_balance_amount_non_negative CHECK (available_amount >= 0),
    CONSTRAINT uq_balance_account_currency UNIQUE (account_id, currency)
);

CREATE INDEX idx_balance_account_id ON balance (account_id);

CREATE TABLE account_transaction (
    transaction_id            UUID           PRIMARY KEY,
    account_id                UUID           NOT NULL REFERENCES account (account_id),
    amount                    NUMERIC(19, 2) NOT NULL,
    currency                  VARCHAR(3)     NOT NULL,
    direction                 VARCHAR(3)     NOT NULL,
    description               VARCHAR(255)   NOT NULL,
    balance_after_transaction NUMERIC(19, 2) NOT NULL,
    created_at                TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_transaction_currency CHECK (currency IN ('EUR', 'SEK', 'GBP', 'USD')),
    CONSTRAINT chk_transaction_direction CHECK (direction IN ('IN', 'OUT')),
    CONSTRAINT chk_transaction_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_transaction_description_not_blank CHECK (TRIM(description) <> '')
);

CREATE INDEX idx_transaction_account_id ON account_transaction (account_id);
CREATE INDEX idx_transaction_account_created_at ON account_transaction (account_id, created_at DESC);
