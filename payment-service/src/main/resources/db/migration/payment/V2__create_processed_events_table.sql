CREATE TABLE processed_events (
    id BIGSERIAL PRIMARY KEY,
    causation_id UUID NOT NULL,
    aggregate_id UUID NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

CREATE UNIQUE INDEX idx_causation_id_unique ON processed_events (causation_id);

CREATE INDEX idx_aggregate_id ON processed_events (aggregate_id);

CREATE TABLE pending_messages (
    id UUID PRIMARY KEY,
    topic TEXT NOT NULL,
    key TEXT,
    payload_type TEXT NOT NULL,
    payload JSONB NOT NULL,
    headers JSONB,
    status TEXT NOT NULL DEFAULT 'PENDING',
    retries INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    sent_at TIMESTAMPTZ
);

CREATE INDEX idx_pending_messages_status_created_at
ON pending_messages (status, created_at);
