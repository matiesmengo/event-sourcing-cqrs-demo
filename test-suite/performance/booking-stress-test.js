import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomItem } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
    stages: [
        { duration: '30s', target: 20 },    // Ramping up
        { duration: '2m', target: 100 },    // Constant stress
        { duration: '30s', target: 0 },     // Cool down
    ],
    thresholds: {
        http_req_duration: ['p(95)<200'],
    },
};

const BASE_URL = 'http://host.docker.internal:8080/bookings';

// Pool dataset
const products = [
    { id: 'aaaa0000-aaaa-0000-aaaa-000000000001', name: 'LowPrice-HighStock' },
    { id: 'bbbb0000-bbbb-0000-bbbb-000000000002', name: 'MidStock' },
    { id: 'cccc0000-cccc-0000-cccc-000000000003', name: 'NoStock' },
    { id: 'dddd0000-dddd-0000-dddd-000000000004', name: 'Normal1' },
    { id: 'eeee0000-eeee-0000-eeee-000000000005', name: 'Normal2' },
    { id: 'ffff0000-ffff-0000-ffff-000000000006', name: 'Normal3' }
];

export default function () {
    // Random products
    const selectedProduct = randomItem(products);
    const alsoAddNoStockProduct = Math.random() < 0.15; // 15% to add product without stock

    const items = [{ productId: selectedProduct.id, quantity: 1 }];
    if (alsoAddNoStockProduct) {
        items.push({ productId: 'cccc0000-cccc-0000-cccc-000000000003', quantity: 1 });
    }

    // 10% failure payment
    const paymentOutcome = Math.random() < 0.9 ? 'SUCCESS' : 'FAILURE';

    const payload = JSON.stringify({
        userId: "99999999-0000-0000-0000-999999999999",
        products: items
    });

    const params = {
        headers: {
            'x-forced-payment-outcome': paymentOutcome,
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(BASE_URL, payload, params);

    check(res, {
        'is status 201': (r) => r.status === 201 || r.status === 200,
        'has bookingId': (r) => r.json().bookingId !== undefined,
    });

    sleep(Math.random() * 0.5 + 0.1); // Between 100ms & 600ms
}