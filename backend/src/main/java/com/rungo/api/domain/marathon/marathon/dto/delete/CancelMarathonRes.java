package com.rungo.api.domain.marathon.marathon.dto.delete;


import com.rungo.api.domain.marathon.course.entity.Course;
import com.rungo.api.domain.marathon.marathon.dto.CourseItemRes;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;
import com.rungo.api.domain.registration.entity.Registration;

import java.time.LocalDate;
import java.util.List;

public record CancelMarathonRes(
        Long marathonId,
        String title,
        LocalDate eventDate,
        MarathonStatus status,
        List<CancelCourseItemRes> courses
) {
    public static CancelMarathonRes from(Marathon marathon) {
        return new CancelMarathonRes(
                marathon.getId(),
                marathon.getTitle(),
                marathon.getEventDate(),
                marathon.getStatus(),
                marathon.getCourses().stream()
                        .map(CancelCourseItemRes::from)
                        .toList()

        );
    }
}
