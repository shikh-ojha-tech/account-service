-- Customer bank accounts
CREATE TABLE account (
    account_id   UUID         PRIMARY KEY,              -- unique account id
    customer_id  VARCHAR(64)  NOT NULL,                 -- who owns the account
    country      VARCHAR(2)   NOT NULL,                 -- country code (2 letters)
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()    -- when the account was opened
);

CREATE INDEX idx_account_customer_id ON account (customer_id);

-- Money held on an account, one row per currency
CREATE TABLE balance (
    balance_id       UUID           PRIMARY KEY,                          -- unique balance id
    account_id       UUID           NOT NULL REFERENCES account (account_id), -- which account
    currency         VARCHAR(3)     NOT NULL,                             -- EUR / SEK / GBP / USD
    available_amount NUMERIC(19, 2) NOT NULL DEFAULT 0,                   -- how much is left
    updated_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),               -- last change time
    CONSTRAINT chk_balance_currency CHECK (currency IN ('EUR', 'SEK', 'GBP', 'USD')),
    CONSTRAINT chk_balance_amount_non_negative CHECK (available_amount >= 0),
    CONSTRAINT uq_balance_account_currency UNIQUE (account_id, currency)
);

CREATE INDEX idx_balance_account_id ON balance (account_id);

-- Money moves (IN / OUT) for an account
CREATE TABLE account_transaction (
    transaction_id            UUID           PRIMARY KEY,                          -- unique transaction id
    account_id                UUID           NOT NULL REFERENCES account (account_id), -- which account
    amount                    NUMERIC(19, 2) NOT NULL,                             -- how much moved
    currency                  VARCHAR(3)     NOT NULL,                             -- currency of the move
    direction                 VARCHAR(3)     NOT NULL,                             -- IN or OUT
    description               VARCHAR(255)   NOT NULL,                             -- short note
    balance_after_transaction NUMERIC(19, 2) NOT NULL,                             -- balance after this move
    created_at                TIMESTAMPTZ    NOT NULL DEFAULT NOW(),               -- when it was booked
    CONSTRAINT chk_transaction_currency CHECK (currency IN ('EUR', 'SEK', 'GBP', 'USD')),
    CONSTRAINT chk_transaction_direction CHECK (direction IN ('IN', 'OUT')),
    CONSTRAINT chk_transaction_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_transaction_description_not_blank CHECK (TRIM(description) <> '')
);

CREATE INDEX idx_transaction_account_id ON account_transaction (account_id);
CREATE INDEX idx_transaction_account_created_at ON account_transaction (account_id, created_at DESC);
