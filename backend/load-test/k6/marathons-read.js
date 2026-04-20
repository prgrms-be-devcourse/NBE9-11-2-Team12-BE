import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:8080';

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
    const res = http.get(`${BASE_URL}/api/v1/marathons?page=0&size=10`);

    check(res, {
        'GET /marathons status is 200': (r) => r.status === 200,
    });

    sleep(1);
}