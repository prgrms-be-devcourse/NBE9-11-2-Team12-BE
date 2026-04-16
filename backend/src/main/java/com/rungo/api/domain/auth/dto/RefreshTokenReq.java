package com.rungo.api.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenReq(

        @NotBlank(message = "refreshToken값은 필수입니다.")
        String refreshToken

) {}