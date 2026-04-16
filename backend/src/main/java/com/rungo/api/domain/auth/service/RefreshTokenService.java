package com.rungo.api.domain.auth.service;

import com.rungo.api.domain.auth.entity.RefreshToken;
import com.rungo.api.domain.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    // refreshToken 저장
    public void save(Long userId, String refreshToken) {

        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .build();

        repository.save(token); // 기존 값 자동 덮어쓰기
    }

    // userId로 RefreshToken 조회
    public String getRefreshToken(Long userId) {

        return findByUserId(userId).orElse(null);
    }

    // userId로 RefreshToken 조회
    public Optional<String> findByUserId(Long userId) {
        return repository.findById(userId)
                .map(RefreshToken::getRefreshToken);
    }

    // RefreshToken 삭제 (로그아웃 시)
    public void delete(Long userId) {
        repository.deleteById(userId);
    }
}