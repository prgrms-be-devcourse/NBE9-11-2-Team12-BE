package com.rungo.api.domain.registration.dto;


import com.rungo.api.domain.marathon.marathon.dto.PageRes;
import com.rungo.api.domain.registration.entity.Registration;
import com.rungo.api.domain.registration.enumtype.RegistrationStatus;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MyRegistrationRes(
        List<Item> content,
        PageRes pageRes
) {
    public static MyRegistrationRes from(Page<Registration> page) {
        return new MyRegistrationRes(
                page.getContent().stream()
                        .map(Item::from)
                        .toList(),
                PageRes.from(page)
        );
    }

    public record Item(
            Long registrationId,
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
        public static Item from(Registration registration) {
            return new Item(
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
}