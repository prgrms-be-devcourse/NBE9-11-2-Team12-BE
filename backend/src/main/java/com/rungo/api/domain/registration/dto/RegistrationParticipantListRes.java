package com.rungo.api.domain.registration.dto;

import com.rungo.api.domain.marathon.marathon.dto.PageRes;
import com.rungo.api.domain.registration.entity.Registration;
import com.rungo.api.domain.registration.enumtype.RegistrationStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public record RegistrationParticipantListRes(
        List<Item> content,
        PageRes pageRes
) {

    public static RegistrationParticipantListRes from(Page<Registration> page) {
        return new RegistrationParticipantListRes(
                page.getContent().stream()
                        .map(Item::from)
                        .toList(),
                PageRes.from(page)
        );
    }

    // 참가자 목록 1건
    public record Item(
            Long registrationId,
            String name,
            String phoneNumber,
            String tSize,
            Long courseId,
            String courseType,
            RegistrationStatus status,
            LocalDateTime appliedAt
    ) {
        public static Item from(Registration registration) {
            return new Item(
                    registration.getId(),
                    registration.getSnapName(),
                    registration.getSnapPhoneNumber(),
                    registration.getTSize(),
                    registration.getCourse().getId(),
                    registration.getCourse().getCourseType(),
                    registration.getStatus(),
                    registration.getAppliedAt()
            );
        }
    }
}