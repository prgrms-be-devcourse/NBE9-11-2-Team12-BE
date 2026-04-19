package com.rungo.api.domain.marathon.marathon.dto.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CreateMarathonReq(
        @NotBlank(message = "대회명은 필수입니다.")
        String title,

        @NotBlank(message = "지역은 필수입니다.")
        String region,

        @NotNull(message = "대회 일자는 필수입니다.")
        LocalDate eventDate,

        String posterImageUrl,

        @NotNull(message = "접수 시작일시는 필수입니다.")
        LocalDateTime registrationStartAt,

        @NotNull(message = "접수 종료일시는 필수입니다.")
        LocalDateTime registrationEndAt,


        @Valid
        @NotEmpty(message = "코스는 최소 1개 이상 등록해야 합니다.")
        List<CreateCourseItemReq> courses
) {
    public record CreateCourseItemReq(
            @NotBlank(message = "코스 타입은 필수입니다.")
            String courseType,

            @NotNull(message = "참가비는 필수입니다.")
            @Min(value = 0, message = "참가비는 0 이상이어야 합니다.")
            BigDecimal price,

            @NotNull(message = "정원은 필수입니다.")
            @Min(value = 1, message = "정원은 1 이상이어야 합니다.")
            Integer capacity
    ) {
    }
}
