package com.rungo.api.domain.marathon.marathon.dto;

import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CreateMarathonRes(
        Long id,
        String title,
        String region,
        LocalDate eventDate,
        String posterImageUrl,
        LocalDateTime registrationStartAt,
        LocalDateTime registrationEndAt,
        MarathonStatus status,
        List<CourseItemRes> courses,
        LocalDateTime createdAt
) {

    public record CourseItemRes(
            Long id,
            String courseType,
            BigDecimal price,
            Integer capacity,
            Integer currentCount,
            Integer remainingCount
    ) {
    }
}