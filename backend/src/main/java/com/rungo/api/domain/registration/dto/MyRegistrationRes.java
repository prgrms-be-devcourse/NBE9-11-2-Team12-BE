package com.rungo.api.domain.registration.dto;


import com.rungo.api.domain.registration.entity.Registration;
import com.rungo.api.domain.registration.enumtype.RegistrationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MyRegistrationRes(
        Long id,
        Long marathonId,
        String marathonTitle,
        Long courseId,
        String courseType,
        RegistrationStatus status,
        BigDecimal price,
        LocalDate eventDate,
        String snapName,
        String snapPhoneNumber,
        String snapZipCode,
        String snapAddress,
        String snapDetail,
        String tSize,
        boolean agreedTerms,
        LocalDateTime appliedAt
) {
    public static MyRegistrationRes from(Registration registration) {
        return new MyRegistrationRes(
                registration.getId(),
                registration.getMarathon().getId(),
                registration.getMarathon().getTitle(),
                registration.getCourse().getId(),
                registration.getCourse().getCourseType(),
                registration.getStatus(),
                registration.getCourse().getPrice(),
                registration.getMarathon().getEventDate(),
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