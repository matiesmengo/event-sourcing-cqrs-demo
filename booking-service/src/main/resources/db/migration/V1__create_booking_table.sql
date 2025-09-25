CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE bookings (
    booking_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    resource_id UUID NOT NULL,
    booking_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);
