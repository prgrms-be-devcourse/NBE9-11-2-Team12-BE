package com.rungo.api.domain.marathon.marathon.dto.update;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UpdateMarathonReq(

        String title,
        String region,
        String detailedAddress,
        LocalDate eventDate,
        MultipartFile posterImage,
        LocalDateTime registrationStartAt,
        LocalDateTime registrationEndAt,

        @Valid
        List<UpdateCourseItemReq> courses

) {
    public record UpdateCourseItemReq(

            @NotNull(message = "코스 아이디는 필수입니다.")
            Long id,

            String courseType,

            @Min(value = 0, message = "참가비는 0 이상이어야 합니다.")
            BigDecimal price,
            @Min(value = 1, message = "정원은 1 이상이어야 합니다.")
            Integer capacity
    ) {
    }
}
