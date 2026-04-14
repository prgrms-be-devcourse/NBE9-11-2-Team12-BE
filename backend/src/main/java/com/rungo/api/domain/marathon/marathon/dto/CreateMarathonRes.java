package com.rungo.api.domain.marathon.marathon.dto;

import com.rungo.api.domain.marathon.marathon.entity.Marathon;
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
    public static CreateMarathonRes from(Marathon marathon) {
        return new CreateMarathonRes(
                marathon.getId(),
                marathon.getTitle(),
                marathon.getRegion(),
                marathon.getEventDate(),
                marathon.getPosterImageUrl(),
                marathon.getRegistrationStartAt(),
                marathon.getRegistrationEndAt(),
                marathon.getStatus(),
                marathon.getCourses().stream()
                        .map(CourseItemRes::from)
                        .toList(),
                marathon.getCreatedAt()
        );
    }
}