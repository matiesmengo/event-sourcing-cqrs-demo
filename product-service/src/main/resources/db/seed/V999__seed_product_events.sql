CREATE EXTENSION IF NOT EXISTS "pgcrypto";

INSERT INTO product_events (event_id, aggregate_id, event_type, event_data, aggregate_version, created_at)
VALUES
    (gen_random_uuid(), 'aaaa0000-aaaa-0000-aaaa-000000000001', 'ProductCreatedEvent',
    jsonb_build_object('productId', 'aaaa0000-aaaa-0000-aaaa-000000000001', 'stockTotal', 5000, 'price', 5.50, 'aggregateVersion', 1, 'createdAt', to_char(NOW(), 'YYYY-MM-DD"T"HH24:MI:SS.MS"Z"')), 1, NOW()),

    (gen_random_uuid(), 'bbbb0000-bbbb-0000-bbbb-000000000002', 'ProductCreatedEvent',
    jsonb_build_object('productId', 'bbbb0000-bbbb-0000-bbbb-000000000002', 'stockTotal', 500, 'price', 15.00, 'aggregateVersion', 1, 'createdAt', to_char(NOW(), 'YYYY-MM-DD"T"HH24:MI:SS.MS"Z"')), 1, NOW()),

    (gen_random_uuid(), 'cccc0000-cccc-0000-cccc-000000000003', 'ProductCreatedEvent',
    jsonb_build_object('productId', 'cccc0000-cccc-0000-cccc-000000000003', 'stockTotal', 0, 'price', 99.99, 'aggregateVersion', 1, 'createdAt', to_char(NOW(), 'YYYY-MM-DD"T"HH24:MI:SS.MS"Z"')), 1, NOW()),

    (gen_random_uuid(), 'dddd0000-dddd-0000-dddd-000000000004', 'ProductCreatedEvent',
    jsonb_build_object('productId', 'dddd0000-dddd-0000-dddd-000000000004', 'stockTotal', 2000, 'price', 10.00, 'aggregateVersion', 1, 'createdAt', to_char(NOW(), 'YYYY-MM-DD"T"HH24:MI:SS.MS"Z"')), 1, NOW()),

    (gen_random_uuid(), 'eeee0000-eeee-0000-eeee-000000000005', 'ProductCreatedEvent',
    jsonb_build_object('productId', 'eeee0000-eeee-0000-eeee-000000000005', 'stockTotal', 2000, 'price', 25.00, 'aggregateVersion', 1, 'createdAt', to_char(NOW(), 'YYYY-MM-DD"T"HH24:MI:SS.MS"Z"')), 1, NOW()),

    (gen_random_uuid(), 'ffff0000-ffff-0000-ffff-000000000006', 'ProductCreatedEvent',
    jsonb_build_object('productId', 'ffff0000-ffff-0000-ffff-000000000006', 'stockTotal', 2000, 'price', 50.00, 'aggregateVersion', 1, 'createdAt', to_char(NOW(), 'YYYY-MM-DD"T"HH24:MI:SS.MS"Z"')), 1, NOW());
