CREATE SCHEMA IF NOT EXISTS booking;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE bookings (
    booking_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    resource_id UUID NOT NULL,
    booking_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
