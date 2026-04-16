package com.rungo.api.domain.registration.dto;

import com.rungo.api.domain.registration.entity.Registration;
import com.rungo.api.domain.registration.enumtype.RegistrationStatus;

import java.time.LocalDateTime;

public record RegistrationParticipantRes(
        Long applicationId,
        Long userId,
        String name,
        String phoneNumber,
        String zipCode,
        String address,
        String detailAddress,
        String tSize,
        Long courseId,
        String courseType,
        RegistrationStatus status,
        LocalDateTime appliedAt
) {
    public static RegistrationParticipantRes from(Registration registration) {
        return new RegistrationParticipantRes(
                registration.getId(),
                registration.getUser().getId(),
                registration.getSnapName(),
                registration.getSnapPhoneNumber(),
                registration.getSnapZipCode(),
                registration.getSnapAddress(),
                registration.getSnapDetail(),
                registration.getTSize(),
                registration.getCourse().getId(),
                registration.getCourse().getCourseType(),
                registration.getStatus(),
                registration.getAppliedAt()
        );
    }
}