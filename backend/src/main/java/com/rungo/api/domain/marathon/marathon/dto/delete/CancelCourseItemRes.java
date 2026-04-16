package com.rungo.api.domain.marathon.marathon.dto.delete;

import com.rungo.api.domain.marathon.course.entity.Course;

import java.math.BigDecimal;

public record CancelCourseItemRes(
        Long id,
        String courseType

) {
    public static CancelCourseItemRes from(Course course){
        return new CancelCourseItemRes(
                course.getId(),
                course.getCourseType()
        );
    }
}
