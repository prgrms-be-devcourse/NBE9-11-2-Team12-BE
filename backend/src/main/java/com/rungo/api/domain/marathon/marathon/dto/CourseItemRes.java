package com.rungo.api.domain.marathon.marathon.dto;

import java.math.BigDecimal;

public record CourseItemRes(
        Long id,
        String courseType,
        BigDecimal price,
        Integer capacity,
        Integer currentCount,
        Integer remainingCount
) {
}
