package com.rungo.api.domain.registration.dto;

import com.rungo.api.domain.marathon.course.entity.Course;

public record CourseRegistrationStatusRes(
        Long courseId,
        String courseType,
        Integer currentCount,
        Integer capacity,
        Integer remainingCount,
        Integer recruitmentRate
) {
    public static CourseRegistrationStatusRes from(Course course) {
        int currentCount = course.getCurrentCount();
        int capacity = course.getCapacity();
        int remainingCount = course.getRemainingCount();

        int recruitmentRate = 0;

        if (capacity > 0) {
            recruitmentRate = (int) Math.round((currentCount * 100.0) / capacity);
        }

        return new CourseRegistrationStatusRes(
                course.getId(),
                course.getCourseType(),
                currentCount,
                capacity,
                remainingCount,
                recruitmentRate
        );
    }
}