CREATE TABLE outbox_event (
    id           UUID         PRIMARY KEY,
    payload      JSONB        NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    published_at TIMESTAMPTZ  NULL,
    attempts     INT          NOT NULL DEFAULT 0
);

CREATE INDEX idx_outbox_event_unpublished
    ON outbox_event (created_at ASC)
    WHERE published_at IS NULL;
