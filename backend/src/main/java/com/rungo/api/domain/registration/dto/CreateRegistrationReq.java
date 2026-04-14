package com.rungo.api.domain.registration.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRegistrationReq(
        @NotNull Long courseId,
        @NotBlank String snapZipCode,
        @NotBlank String snapAddress,
        String snapDetail,
        @NotBlank String tSize,
        @AssertTrue boolean agreedTerms
) {
}
