import http from 'k6/http';
import { check, sleep } from 'k6';
import exec from 'k6/execution';

const BASE_URL = 'http://localhost:8080';
const TEST_PASSWORD = 'Password123!';
const COURSE_ID = 16;

// user1@test.com ~ user500@test.com
const USERS = Array.from({ length: 1000 }, (_, i) => `user${i + 1}@test.com`);

export const options = {
    stages: [
        { duration: '10s', target: 5 },
        { duration: '20s', target: 10 },
        { duration: '20s', target: 20 },
        { duration: '10s', target: 0 },
    ],
    thresholds: {
        http_req_failed: ['rate<0.05'],
        http_req_duration: ['p(95)<1500'],
    },
};

function login(email) {
    const payload = JSON.stringify({
        email: email,
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

    if (!res.cookies.accessToken || !res.cookies.refreshToken) {
        throw new Error(`로그인 실패: ${email}`);
    }

    return {
        accessToken: res.cookies.accessToken[0].value,
        refreshToken: res.cookies.refreshToken[0].value,
    };
}

export default function () {
    const globalIteration = exec.scenario.iterationInTest;
    const email = USERS[globalIteration % USERS.length];

    const tokens = login(email);

    const cookies = {
        accessToken: tokens.accessToken,
        refreshToken: tokens.refreshToken,
    };

    const payload = JSON.stringify({
        courseId: COURSE_ID,
        snapZipCode: '12345',
        snapAddress: '서울시 강남구',
        snapDetail: '101동',
        tSize: 'L',
        agreedTerms: true,
    });

    const res = http.post(`${BASE_URL}/api/v1/registrations`, payload, {
        cookies,
        headers: {
            'Content-Type': 'application/json',
        },
    });

    check(res, {
        'registration status is 200 or 201': (r) => r.status === 200 || r.status === 201,
    });

    sleep(1);
}