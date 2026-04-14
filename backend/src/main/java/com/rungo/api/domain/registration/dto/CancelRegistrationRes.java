package com.rungo.api.domain.registration.dto;

import com.rungo.api.domain.registration.entity.Registration;

import java.time.LocalDateTime;

public record CancelRegistrationRes(

        Long registrationId,
        Long marathonId,
        String marathonTitle,
        Long courseId,
        String courseType,
        String status,
        LocalDateTime canceledAt

) {
    public static CancelRegistrationRes from(Registration registration) {
        return new CancelRegistrationRes(
                registration.getId(),
                registration.getMarathon().getId(),
                registration.getMarathon().getTitle(),
                registration.getCourse().getId(),
                registration.getCourse().getCourseType(),
                registration.getStatus().name(),
                registration.getCanceledAt()
        );
    }
}
