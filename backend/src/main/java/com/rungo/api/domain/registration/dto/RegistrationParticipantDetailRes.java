package com.rungo.api.domain.registration.dto;

import com.rungo.api.domain.registration.entity.Registration;
import com.rungo.api.domain.registration.enumtype.RegistrationStatus;

import java.time.LocalDateTime;

public record RegistrationParticipantDetailRes(
        Long registrationId,
        Long marathonId,
        String marathonTitle,
        Long courseId,
        String courseType,
        RegistrationStatus status,

        String snapName,
        String snapPhoneNumber,
        String snapZipCode,
        String snapAddress,
        String snapDetail,

        String tSize,
        boolean agreedTerms,
        LocalDateTime appliedAt
) {
    public static RegistrationParticipantDetailRes from(Registration registration) {
        return new RegistrationParticipantDetailRes(
                registration.getId(),
                registration.getMarathon().getId(),
                registration.getMarathon().getTitle(),
                registration.getCourse().getId(),
                registration.getCourse().getCourseType(),
                registration.getStatus(),

                registration.getSnapName(),
                registration.getSnapPhoneNumber(),
                registration.getSnapZipCode(),
                registration.getSnapAddress(),
                registration.getSnapDetail(),

                registration.getTSize(),
                registration.isAgreedTerms(),
                registration.getAppliedAt()
        );
    }
}