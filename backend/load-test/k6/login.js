import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:8080';
const TEST_EMAIL = 'user@test.com';
const TEST_PASSWORD = 'Password123!';

export const options = {
    stages: [
        { duration: '10s', target: 10 },
        { duration: '20s', target: 30 },
        { duration: '20s', target: 50 },
        { duration: '10s', target: 0 },
    ],
    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<1000'],
    },
};

export default function () {
    const payload = JSON.stringify({
        email: TEST_EMAIL,
        password: TEST_PASSWORD,
    });

    const res = http.post(`${BASE_URL}/api/v1/auth/login`, payload, {
        headers: {
            'Content-Type': 'application/json',
        },
    });

    check(res, {
        'login status is 200': (r) => r.status === 200,
        'accessToken cookie exists': (r) => !!r.cookies.accessToken,
        'refreshToken cookie exists': (r) => !!r.cookies.refreshToken,
    });

    sleep(1);
}