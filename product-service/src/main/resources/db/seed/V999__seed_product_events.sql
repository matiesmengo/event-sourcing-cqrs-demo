CREATE EXTENSION IF NOT EXISTS "pgcrypto";

INSERT INTO product_events (
    event_id,
    aggregate_id,
    event_type,
    event_data,
    aggregate_version,
    created_at
)
VALUES
    (
        gen_random_uuid(),
        '11111111-1111-1111-1111-111111111111',
        'ProductCreatedEvent',
        jsonb_build_object(
            'productId', '11111111-1111-1111-1111-111111111111',
            'stockTotal', 50,
            'price', 10.01,
            'aggregateVersion', 1,
            'createdAt', to_char(NOW(), 'YYYY-MM-DD"T"HH24:MI:SS.MS"Z"')
        ),
        1,
        NOW()
    ),
    (
        gen_random_uuid(),
        '22222222-2222-2222-2222-222222222222',
        'ProductCreatedEvent',
        jsonb_build_object(
            'productId', '22222222-2222-2222-2222-222222222222',
            'stockTotal', 150,
            'price', 20.50,
            'aggregateVersion', 1,
            'createdAt', to_char(NOW(), 'YYYY-MM-DD"T"HH24:MI:SS.MS"Z"')
        ),
        1,
        NOW()
    ),
    (
        gen_random_uuid(),
        '33333333-3333-3333-3333-333333333333',
        'ProductCreatedEvent',
        jsonb_build_object(
            'productId', '33333333-3333-3333-3333-333333333333',
            'stockTotal', 0,
            'price', 30.99,
            'aggregateVersion', 1,
            'createdAt', to_char(NOW(), 'YYYY-MM-DD"T"HH24:MI:SS.MS"Z"')
        ),
        1,
        NOW()
    );