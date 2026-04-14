package com.rungo.api.domain.registration.dto;

import com.rungo.api.domain.registration.entity.Registration;

import java.time.LocalDateTime;

public record CreateRegistrationRes(
        Long registrationId,
        Long marathonId,
        Long courseId,
        String status,
        LocalDateTime appliedAt

) {
    public static CreateRegistrationRes from(Registration registration) {
        return new CreateRegistrationRes(
                registration.getId(),
                registration.getMarathon().getId(),
                registration.getCourse().getId(),
                registration.getStatus().name(),
                registration.getAppliedAt()
        );
    }
}
