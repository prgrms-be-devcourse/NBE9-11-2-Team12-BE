package com.rungo.api.domain.registration.dto;

import java.util.List;

public record RegistrationOverviewRes(
        List<RegistrationParticipantRes> participants,
        List<CourseRegistrationStatusRes> courseStatuses
) {
    public static RegistrationOverviewRes of(
            List<RegistrationParticipantRes> participants,
            List<CourseRegistrationStatusRes> courseStatuses
    ) {
        return new RegistrationOverviewRes(participants, courseStatuses);
    }
}