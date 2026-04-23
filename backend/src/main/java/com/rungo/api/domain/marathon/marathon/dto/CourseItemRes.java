package com.rungo.api.domain.marathon.marathon.dto;

import com.rungo.api.domain.marathon.course.entity.Course;
import com.rungo.api.domain.marathon.course.status.CourseStatus;

import java.math.BigDecimal;

public record CourseItemRes(
        Long id,
        String courseType,
        BigDecimal price,
        Integer capacity,
        Integer currentCount,
        Integer remainingCount,
        CourseStatus status
) {
    public static CourseItemRes from(Course course) {
        return new CourseItemRes(
                course.getId(),
                course.getCourseType(),
                course.getPrice(),
                course.getCapacity(),
                course.getCurrentCount(),
                course.getRemainingCount(),
                course.getStatus()
        );
    }
}
