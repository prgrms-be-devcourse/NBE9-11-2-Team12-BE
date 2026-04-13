package com.rungo.api.domain.application.dto;

import com.rungo.api.domain.application.entity.Application;

import java.time.LocalDateTime;

public record CreateApplicationRes(
        Long applicationId,
        Long marathonId,
        Long courseId,
        String status,
        LocalDateTime appliedAt

) {
    public static CreateApplicationRes from(Application application) {
        return new CreateApplicationRes(
                application.getId(),
                application.getMarathon().getId(),
                application.getCourse().getId(),
                application.getStatus().name(),
                application.getAppliedAt()
        );
    }
}