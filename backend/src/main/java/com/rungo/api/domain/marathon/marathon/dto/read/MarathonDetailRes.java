package com.rungo.api.domain.marathon.marathon.dto.read;

import com.rungo.api.domain.marathon.marathon.dto.CourseItemRes;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MarathonDetailRes(
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

    public static MarathonDetailRes from(Marathon marathon) {
        return new MarathonDetailRes(
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