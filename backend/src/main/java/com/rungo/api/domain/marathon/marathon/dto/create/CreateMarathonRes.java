package com.rungo.api.domain.marathon.marathon.dto.create;

import com.rungo.api.domain.marathon.marathon.dto.CourseItemRes;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CreateMarathonRes(
        Long id,
        String title,
        String region,
        String detailedAddress,
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
                marathon.getDetailedAddress(),
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