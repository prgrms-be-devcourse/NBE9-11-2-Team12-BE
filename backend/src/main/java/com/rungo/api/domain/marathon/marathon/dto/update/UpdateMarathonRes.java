package com.rungo.api.domain.marathon.marathon.dto.update;

import com.rungo.api.domain.marathon.marathon.dto.CourseItemRes;
import com.rungo.api.domain.marathon.marathon.dto.create.CreateMarathonRes;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UpdateMarathonRes(
        Long id,
        String title,
        String region,
        LocalDate eventDate,
        String posterImageUrl,
        LocalDateTime registrationStartAt,
        LocalDateTime registrationEndAt,
        MarathonStatus status,
        List<CourseItemRes> courses,
        LocalDateTime updatedAt
) {
    public static UpdateMarathonRes from(Marathon marathon) {
        return new UpdateMarathonRes(
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
                LocalDateTime.now()
        );
    }
}

