package com.rungo.api.domain.registration.dto;


import com.rungo.api.domain.marathon.course.entity.Course;
import com.rungo.api.domain.marathon.marathon.dto.PageRes;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.registration.entity.Registration;
import com.rungo.api.domain.registration.entity.RegistrationCancelHistory;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record MyRegistrationRes(
        List<Item> content,
        PageRes pageRes
) {

    // 정상 접수 목록 DTO 변환
    public static MyRegistrationRes fromActive(Page<Registration> page) {
        return new MyRegistrationRes(
                page.getContent().stream()
                        .map(Item::fromActive)
                        .toList(),
                PageRes.from(page)
        );
    }

    // 접수 취소 목록 DTO 변환
    public static MyRegistrationRes fromCanceled(
            Page<RegistrationCancelHistory> page,
            Map<Long, Marathon> marathonMap,
            Map<Long, Course> courseMap
    ) {
        return new MyRegistrationRes(
                page.getContent().stream()
                        .map(history -> Item.fromCanceled(
                                history,
                                marathonMap.get(history.getMarathonId()),
                                courseMap.get(history.getCourseId())
                        ))
                        .toList(),
                PageRes.from(page)
        );
    }

    // ACTIVE, CANCELED 공통 구조 => 일부 필드는 null
    public record Item(
            Long registrationId,
            Long historyId,
            Long marathonId,
            String marathonTitle,
            Long courseId,
            String courseType,
            String status,
            BigDecimal price,
            LocalDate eventDate,

            String snapName,
            String snapPhoneNumber,
            String snapZipCode,
            String snapAddress,
            String snapDetail,
            String tSize,
            Boolean agreedTerms,

            LocalDateTime appliedAt,
            LocalDateTime canceledAt
    ) {
        // 정상 접수 Entity를 응답용 item 변환 (취소 전용 필드는 null : originalRegistrationId, canceledAt)
        public static Item fromActive(Registration registration) {
            return new Item(
                    registration.getId(),
                    null,
                    registration.getMarathon().getId(),
                    registration.getMarathon().getTitle(),
                    registration.getCourse().getId(),
                    registration.getCourse().getCourseType(),
                    "ACTIVE",
                    registration.getCourse().getPrice(),
                    registration.getMarathon().getEventDate(),

                    registration.getSnapName(),
                    registration.getSnapPhoneNumber(),
                    registration.getSnapZipCode(),
                    registration.getSnapAddress(),
                    registration.getSnapDetail(),
                    registration.getTSize(),
                    registration.isAgreedTerms(),

                    registration.getAppliedAt(),
                    null
            );
        }

        // 취소 접수 Entity를 응답용 item 변환
        public static Item fromCanceled(
                RegistrationCancelHistory history,
                Marathon marathon,
                Course course
        ) {
            return new Item(
                    history.getId(),
                    history.getOriginalRegistrationId(),
                    history.getMarathonId(),
                    marathon != null ? marathon.getTitle() : null,
                    history.getCourseId(),
                    course != null ? course.getCourseType() : null,
                    "CANCELED",
                    course != null ? course.getPrice() : null,
                    marathon != null ? marathon.getEventDate() : null,

                    history.getSnapName(),
                    history.getSnapPhoneNumber(),
                    history.getSnapZipCode(),
                    history.getSnapAddress(),
                    history.getSnapDetail(),
                    history.getTSize(),
                    history.isAgreedTerms(),

                    history.getAppliedAt(),
                    history.getCanceledAt()
            );
        }
    }
}