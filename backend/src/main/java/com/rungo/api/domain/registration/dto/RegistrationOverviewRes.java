package com.rungo.api.domain.registration.dto;

import com.rungo.api.domain.marathon.course.entity.Course;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RegistrationOverviewRes(
        MarathonInfo marathon,
        List<CourseStatus> courseStatuses
) {

    public static RegistrationOverviewRes of(Marathon marathon, List<Course> courses) {
        List<CourseStatus> courseStatuses = courses.stream()
                .map(CourseStatus::from)
                .toList();

        int totalCurrentCount = courses.stream()
                .mapToInt(Course::getCurrentCount)
                .sum();

        int totalCapacity = courses.stream()
                .mapToInt(Course::getCapacity)
                .sum();

        return new RegistrationOverviewRes(
                MarathonInfo.of(marathon, totalCurrentCount, totalCapacity),
                courseStatuses
        );
    }

    // 마라톤 요약 정보
    public record MarathonInfo(
            Long marathonId,
            String marathonTitle,
            LocalDate eventDate,
            String region,
            int totalCurrentCount,
            int totalCapacity,
            int totalRemainingCount,
            int totalRecruitmentRate
    ) {
        public static MarathonInfo of(Marathon marathon, int totalCurrentCount, int totalCapacity) {
            int totalRemainingCount = Math.max(totalCapacity - totalCurrentCount, 0);

            return new MarathonInfo(
                    marathon.getId(),
                    marathon.getTitle(),
                    marathon.getEventDate(),
                    marathon.getRegion(),
                    totalCurrentCount,
                    totalCapacity,
                    totalRemainingCount,
                    calculateRate(totalCurrentCount, totalCapacity)
            );
        }
    }

    // 코스별 현황
    public record CourseStatus(
            Long courseId,
            String courseType,
            BigDecimal price,
            int currentCount,
            int capacity,
            int remainingCount,
            int recruitmentRate
    ) {
        public static CourseStatus from(Course course) {
            int currentCount = course.getCurrentCount();
            int capacity = course.getCapacity();
            int remainingCount = Math.max(capacity - currentCount, 0);

            return new CourseStatus(
                    course.getId(),
                    course.getCourseType(),
                    course.getPrice(),
                    currentCount,
                    capacity,
                    remainingCount,
                    calculateRate(currentCount, capacity)
            );
        }
    }

    private static int calculateRate(int currentCount, int capacity) {
        if (capacity <= 0) {
            return 0;
        }
        return (int) ((currentCount * 100.0) / capacity);
    }
}