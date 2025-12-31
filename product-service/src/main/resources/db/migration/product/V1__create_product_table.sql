CREATE SCHEMA IF NOT EXISTS product;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE product_events (
    event_id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL,
    event_type TEXT NOT NULL,
    event_data JSONB NOT NULL,
    aggregate_version INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_product_aggregate_version UNIQUE (aggregate_id, aggregate_version)
);

CREATE INDEX idx_product_events_aggregate ON product_events (aggregate_id, aggregate_version ASC);
