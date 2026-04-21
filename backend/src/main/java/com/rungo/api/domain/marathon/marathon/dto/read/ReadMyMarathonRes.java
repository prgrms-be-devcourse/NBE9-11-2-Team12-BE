package com.rungo.api.domain.marathon.marathon.dto.read;

import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ReadMyMarathonRes(
        Long id,
        String title,
        String region,
        LocalDate eventDate,
        String posterImageUrl,
        LocalDateTime registrationStartAt,
        LocalDateTime registrationEndAt,
        MarathonStatus status,
        List<CourseSummary>courses


) {
    public record CourseSummary(

            Long courseId,
            String courseType,
            BigDecimal price,
            int capacity,
            int currentCount

    ){}
    public static ReadMyMarathonRes from(Marathon marathon) {

        return new ReadMyMarathonRes(

                marathon.getId(),
                marathon.getTitle(),
                marathon.getRegion(),
                marathon.getEventDate(),
                marathon.getPosterImageUrl(),
                marathon.getRegistrationStartAt(),
                marathon.getRegistrationEndAt(),
                marathon.getStatus(),
                marathon.getCourses().stream()
                        .map(course -> new CourseSummary(
                                course.getId(),
                                course.getCourseType(),
                                course.getPrice(),
                                course.getCapacity(),
                                course.getCurrentCount()
                        ))
                        .toList()

        );

    }

}
