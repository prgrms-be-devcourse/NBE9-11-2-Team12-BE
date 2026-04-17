package com.rungo.api.domain.auth.dto;

public record TokenRes(

        String accessToken,
        String refreshToken

) {}