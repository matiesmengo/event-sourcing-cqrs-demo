CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE payments (
    payment_id UUID PRIMARY KEY,
    booking_id UUID NOT NULL,
    payment_type VARCHAR(20) NOT NULL,
    reference VARCHAR(255),
    reason VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE UNIQUE INDEX idx_payments_booking_id ON payments(booking_id);
