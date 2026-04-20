import http from 'k6/http';
import { check, sleep } from 'k6';
import exec from 'k6/execution';

const BASE_URL = 'http://localhost:8080';
const ORGANIZER_EMAIL = 'organizer@test.com';
const ORGANIZER_PASSWORD = 'Password123!';

// 취소 테스트용 마라톤 id: 17 ~ 116
const MARATHON_IDS = Array.from({ length: 100 }, (_, i) => 17 + i);

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

function login() {
    const payload = JSON.stringify({
        email: ORGANIZER_EMAIL,
        password: ORGANIZER_PASSWORD,
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
        throw new Error('주최자 로그인 실패');
    }

    return {
        accessToken: res.cookies.accessToken[0].value,
        refreshToken: res.cookies.refreshToken[0].value,
    };
}

export default function () {
    const globalIteration = exec.scenario.iterationInTest;
    const marathonId = MARATHON_IDS[globalIteration % MARATHON_IDS.length];

    const tokens = login();

    const cookies = {
        accessToken: tokens.accessToken,
        refreshToken: tokens.refreshToken,
    };

    const res = http.patch(
        `${BASE_URL}/api/v1/marathons/${marathonId}/cancel`,
        null,
        {
            cookies,
            headers: {
                'Content-Type': 'application/json',
            },
        }
    );

    check(res, {
        'cancel status is 200': (r) => r.status === 200,
    });

    sleep(1);
}