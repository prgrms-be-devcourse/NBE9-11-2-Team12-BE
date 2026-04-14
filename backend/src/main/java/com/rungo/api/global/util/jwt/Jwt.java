package com.rungo.api.global.util.jwt;

import com.rungo.api.domain.users.enumtype.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class Jwt {
    public static class jwt {

        // jwt 토큰 생성
        public static String toString(String secret, long expireSeconds, Map<String, Object> body) {
            ClaimsBuilder claimsBuilder = Jwts.claims();

            for (Map.Entry<String, Object> entry : body.entrySet()) {
                claimsBuilder.add(entry.getKey(), entry.getValue());
            }

            Claims claims = claimsBuilder.build();

            Date issuedAt = new Date();
            Date expiration = new Date(issuedAt.getTime() + 1000L * expireSeconds);

            Key secretKey = Keys.hmacShaKeyFor(secret.getBytes());

            String jwt = Jwts.builder()
                    .claims(claims)
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .signWith(secretKey)
                    .compact();

            return jwt;
        }

        // jwt 토큰 유효성 검증
        public static boolean isValid(String jwt, String secret) {

            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);

            try {
                Jwts
                        .parser()
                        .verifyWith(secretKey)
                        .build()
                        .parse(jwt)
                        .getPayload();

                return true;
            } catch (Exception e) {
                return false;
            }
        }

        // jwt 토큰에서 사용자 정보 추출
        public static Map<String, Object> payloadOrNull(String jwt, String secret) {
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);

            if(isValid(jwt, secret)) {
                return (Map<String, Object>)Jwts
                        .parser()
                        .verifyWith(secretKey)
                        .build()
                        .parse(jwt)
                        .getPayload();
            }

            return null;
        }

        // JWT 토큰 생성
        public static String generateToken(String username, Role role, String secret) {
            Map<String, Object> body = Map.of("sub", username, "role", role); // username와 role로 사용자 구분
            return toString(secret, 3600, body); // 1시간 만료
        }

        // JWT 토큰에서 클레임 추출
        public static Claims getClaims(String token, String secret) {
            Map<String, Object> payload = payloadOrNull(token, secret);
            if (payload == null) return null;

            Claims claims = Jwts.claims().build();
            claims.putAll(payload);
            return claims;
        }

        // JWT 토큰 유효성 검증
        public static boolean validateToken(String token, String secret) {
            return isValid(token, secret);
        }

        // JWT 토큰에서 사용자 이름 추출
        public static String getUsername(String token, String secret) {
            Claims claims = getClaims(token, secret);
            return claims != null ? claims.getSubject() : null;
        }

        // JWT 토큰에서 역할 추출
        public static String getRole(String token, String secret) {
            Claims claims = getClaims(token, secret);
            return claims != null ? (String) claims.get("role") : null;
        }
    }
}
