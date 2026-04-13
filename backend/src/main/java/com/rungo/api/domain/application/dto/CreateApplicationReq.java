package com.rungo.api.domain.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateApplicationReq(
        @NotNull Long courseId,
        @NotBlank String snapZipCode,
        @NotBlank String snapAddress,
        String snapDetail,
        @NotBlank String tSize,
        @NotNull Boolean agreedTerms
) {}