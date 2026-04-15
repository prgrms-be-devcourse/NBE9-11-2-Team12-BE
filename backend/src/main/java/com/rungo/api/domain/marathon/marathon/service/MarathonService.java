package com.rungo.api.domain.marathon.marathon.service;

import com.rungo.api.domain.marathon.course.entity.Course;
import com.rungo.api.domain.marathon.marathon.dto.CreateMarathonReq;
import com.rungo.api.domain.marathon.marathon.dto.CreateMarathonRes;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;
import com.rungo.api.domain.marathon.marathon.repository.MarathonRepository;
import com.rungo.api.domain.users.entity.Users;
import com.rungo.api.domain.users.enumtype.Role;
import com.rungo.api.domain.users.repository.UserRepository;
import com.rungo.api.global.exception.CustomException;
import com.rungo.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MarathonService {
    private final MarathonRepository marathonRepository;
    private final UserRepository userRepository;
    @Transactional
    public CreateMarathonRes createMarathon(Long id, CreateMarathonReq req) {

        // 주최하는 사람이 존재하는지 확인
        Users organizer = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 주최자 측 인가 확인
        if (organizer.getRole() != Role.ORGANIZER) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }



        // 대회 접수 시작일이 종료일보다 이후이면 예외 처리
        if (req.registrationStartAt().isAfter(req.registrationEndAt())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 대회 개최일이 종료일 보다 이전이면 예외 처리
        if (req.eventDate().isBefore(req.registrationEndAt().toLocalDate())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        //코스 타입 중복이면 예외 처리
        Set<String> courseTypes = new HashSet<>();
        for (CreateMarathonReq.CreateCourseItemReq courseReq : req.courses()) {


            if (!courseTypes.add(normalizeCourseType(courseReq.courseType()))) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
        }
        Marathon marathon = new Marathon(
                organizer,
                req.title(),
                req.region(),
                req.eventDate(),
                req.posterImageUrl(),
                req.registrationStartAt(),
                req.registrationEndAt(),
                MarathonStatus.OPEN
        );

        for (CreateMarathonReq.CreateCourseItemReq courseReq : req.courses()) {
            Course course = new Course(
                    normalizeCourseType(courseReq.courseType()),
                    courseReq.price(),
                    courseReq.capacity(),
                    0
            );

            marathon.addCourse(course);
        }
        Marathon savedMarathon = marathonRepository.save(marathon);
        return CreateMarathonRes.from(savedMarathon);
    }

    // 5k -> 5K, 10k -> 10K, " 5k " -> 5K 로 저장하기 위해 정규화하는 함수
    private String normalizeCourseType(String courseType) {
        if (courseType == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        return courseType.trim().toUpperCase();
    }

}
