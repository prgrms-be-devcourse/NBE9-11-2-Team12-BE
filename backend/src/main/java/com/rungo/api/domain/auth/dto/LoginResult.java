package com.rungo.api.domain.auth.dto;

public record LoginResult(
        
        String accessToken,
        String refreshToken,
        LoginRes loginRes
) {}