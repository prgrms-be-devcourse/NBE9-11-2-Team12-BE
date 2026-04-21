package com.rungo.api.domain.registration.dto;

import com.rungo.api.domain.registration.entity.Registration;

import java.time.LocalDateTime;

public record CreateRegistrationRes(
        Long registrationId,
        Long marathonId,
        String marathonTitle,
        Long courseId,
        String courseType,
        String status,
        LocalDateTime appliedAt

) {
    public static CreateRegistrationRes from(Registration registration) {
        return new CreateRegistrationRes(
                registration.getId(),
                registration.getMarathon().getId(),
                registration.getMarathon().getTitle(),
                registration.getCourse().getId(),
                registration.getCourse().getCourseType(),
                registration.getStatus().name(),
                registration.getAppliedAt()
        );
    }
}
