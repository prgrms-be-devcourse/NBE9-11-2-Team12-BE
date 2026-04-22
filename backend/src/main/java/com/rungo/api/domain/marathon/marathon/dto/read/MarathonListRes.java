package com.rungo.api.domain.marathon.marathon.dto.read;


import com.rungo.api.domain.marathon.course.entity.Course;
import com.rungo.api.domain.marathon.marathon.dto.PageRes;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;
import com.rungo.api.domain.marathon.marathon.enumtype.RecruitmentStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MarathonListRes(
        List<Item> content,
        PageRes pageRes
) {

    public static MarathonListRes from(Page<Marathon> page) {
        return new MarathonListRes(
                page.getContent().stream()
                        .map(Item::from)
                        .toList(),
                PageRes.from(page)
        );
    }

    public record Item(
            Long id,
            String title,
            String region,
            LocalDate eventDate,
            String posterImageUrl,
            LocalDateTime registrationStartAt,
            LocalDateTime registrationEndAt,
            MarathonStatus status,
            int totalCapacity,
            int totalCurrentCount,
            RecruitmentStatus recruitmentStatus
    ) {
        public static Item from(Marathon marathon) {
            int totalCapacity = marathon.getCourses().stream()
                    .mapToInt(Course::getCapacity)
                    .sum();

            int totalCurrentCount = marathon.getCourses().stream()
                    .mapToInt(Course::getCurrentCount)
                    .sum();

            return new Item(

                    marathon.getId(),
                    marathon.getTitle(),
                    marathon.getRegion(),
                    marathon.getEventDate(),
                    marathon.getPosterImageUrl(),
                    marathon.getRegistrationStartAt(),
                    marathon.getRegistrationEndAt(),
                    marathon.getStatus(),
                    totalCapacity,
                    totalCurrentCount,
                    marathon.getRecruitmentStatus()
            );
        }
    }
}
