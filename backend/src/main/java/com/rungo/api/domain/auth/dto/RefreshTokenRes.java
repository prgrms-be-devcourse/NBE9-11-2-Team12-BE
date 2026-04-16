package com.rungo.api.domain.auth.dto;

public record RefreshTokenRes(

        String accessToken // accessToken만 재발급

) {}