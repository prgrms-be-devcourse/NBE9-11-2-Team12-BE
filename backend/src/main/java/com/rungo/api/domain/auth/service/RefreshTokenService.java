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

    public void save(Long userId, String refreshToken) {

        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .build();

        repository.save(token); // 기존 값 자동 덮어쓰기
    }

    public Optional<String> findByUserId(Long userId) {
        return repository.findById(userId)
                .map(RefreshToken::getRefreshToken);
    }

    public void delete(Long userId) {
        repository.deleteById(userId);
    }
}