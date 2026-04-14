package com.rungo.api.domain.marathon.marathon.dto;

import com.rungo.api.domain.marathon.course.entity.Course;

import java.math.BigDecimal;

public record CourseItemRes(
        Long id,
        String courseType,
        BigDecimal price,
        Integer capacity,
        Integer currentCount,
        Integer remainingCount
) {
    public static CourseItemRes from(Course course) {
        return new CourseItemRes(
                course.getId(),
                course.getCourseType(),
                course.getPrice(),
                course.getCapacity(),
                course.getCurrentCount(),
                course.getCapacity() - course.getCurrentCount()
        );
    }
}
