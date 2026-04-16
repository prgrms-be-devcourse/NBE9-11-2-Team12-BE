package com.rungo.api.domain.marathon.marathon.service;

import com.rungo.api.domain.marathon.course.entity.Course;
import com.rungo.api.domain.marathon.marathon.dto.create.CreateMarathonReq;
import com.rungo.api.domain.marathon.marathon.dto.create.CreateMarathonRes;
import com.rungo.api.domain.marathon.marathon.dto.delete.CancelMarathonRes;
import com.rungo.api.domain.marathon.marathon.dto.view.MarathonDetailRes;
import com.rungo.api.domain.marathon.marathon.dto.view.MarathonListRes;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;
import com.rungo.api.domain.marathon.marathon.repository.MarathonRepository;
import com.rungo.api.domain.registration.entity.Registration;
import com.rungo.api.domain.registration.repository.RegistrationRepository;
import com.rungo.api.domain.users.entity.Users;
import com.rungo.api.domain.users.enumtype.Role;
import com.rungo.api.domain.users.repository.UserRepository;
import com.rungo.api.global.exception.CustomException;
import com.rungo.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.message.LoggerNameAwareMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MarathonService {
    private final MarathonRepository marathonRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;

    @Transactional
    public CreateMarathonRes createMarathon(Long id, CreateMarathonReq req) {

        Users organizer = findOrganizer(id);

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

    @Transactional(readOnly = true)
    public MarathonDetailRes getMarathonDetail(Long marathonId) {
        Marathon marathon = getMarathonOrThrow(marathonId);
        if(marathon.getStatus() == MarathonStatus.CANCELED) {
            throw new CustomException(ErrorCode.MARATHON_CANCELED);
        }
        return MarathonDetailRes.from(marathon);
    }

    @Transactional(readOnly = true)
    public MarathonListRes getMarathons(Pageable pageable) {
        Page<Marathon> page = marathonRepository.findByStatusIn(
                List.of(MarathonStatus.TEMP, MarathonStatus.OPEN),
                pageable
        );
        return MarathonListRes.from(page);
    }

    @Transactional
    public CancelMarathonRes cancelMarathon(Long id, Long marathonId){
        Users organizer = findOrganizer(id);

        Marathon marathon = getMarathonOrThrow(marathonId);

        //자기 자신이 신청한 마라톤만 취소할 수 있도록 예외 처리
        if(marathon.getOrganizer().getId() != organizer.getId()){
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        marathon.cancel();
        List<Registration> registrations = registrationRepository.findAllByMarathonId(marathonId);
        for(Registration registration : registrations){
            registration.cancelByOrg();
        }
        return CancelMarathonRes.from(marathon);

    }
    // 5k -> 5K, 10k -> 10K, " 5k " -> 5K 로 저장하기 위해 정규화하는 함수
    private String normalizeCourseType(String courseType) {
        if (courseType == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        return courseType.trim().toUpperCase();
    }

    private Marathon getMarathonOrThrow(Long marathonId){
        return marathonRepository.findById(marathonId)
                .orElseThrow(() -> new CustomException(ErrorCode.MARATHON_NOT_FOUND));
    }

    //id로 주최자 조회 함수, 존재하지 않거나 주최자가 아니면 예외 처리
    private Users findOrganizer(Long id){
        // 주최하는 사람이 존재하는지 확인
        Users organizer = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 주최자 측 인가 확인
        if (organizer.getRole() != Role.ORGANIZER) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        return organizer;
    }

}
