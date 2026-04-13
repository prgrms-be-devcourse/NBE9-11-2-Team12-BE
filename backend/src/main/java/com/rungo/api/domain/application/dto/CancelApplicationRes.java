package com.rungo.api.domain.application.dto;

import com.rungo.api.domain.application.entity.Application;

import java.time.LocalDateTime;

public record CancelApplicationRes(

        Long applicationId,
        Long marathonId,
        String marathonTitle,
        Long courseId,
        String courseType,
        String status,
        LocalDateTime canceledAt

) {
    public static CancelApplicationRes from(Application application) {
        return new CancelApplicationRes(
                application.getId(),
                application.getMarathon().getId(),
                application.getMarathon().getTitle(),
                application.getCourse().getId(),
                application.getCourse().getCourseType(),
                application.getStatus().name(),
                application.getCanceledAt()
        );
    }
}
