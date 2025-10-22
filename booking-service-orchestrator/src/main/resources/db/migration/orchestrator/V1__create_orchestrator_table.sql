CREATE SCHEMA IF NOT EXISTS orchestrator;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE orchestrator_events (
    event_id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL,
    event_type TEXT NOT NULL,
    event_data JSONB NOT NULL,
    aggregate_version INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
