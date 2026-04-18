package com.rungo.api.domain.registration.service;

import com.rungo.api.domain.marathon.course.repository.CourseRepository;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.repository.MarathonRepository;
import com.rungo.api.domain.registration.dto.CourseRegistrationStatusRes;
import com.rungo.api.domain.registration.dto.MyRegistrationRes;
import com.rungo.api.domain.registration.dto.RegistrationOverviewRes;
import com.rungo.api.domain.registration.dto.RegistrationParticipantRes;
import com.rungo.api.domain.registration.entity.Registration;
import com.rungo.api.domain.registration.enumtype.RegistrationStatus;
import com.rungo.api.domain.registration.repository.RegistrationRepository;
import com.rungo.api.global.exception.CustomException;
import com.rungo.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationReadService {

    private final RegistrationRepository registrationRepository;
    private final MarathonRepository marathonRepository;
    private final CourseRepository courseRepository;

    // 내 접수 목록 조회
    @Transactional(readOnly = true)
    public MyRegistrationRes getMyRegistrations(Long userId, RegistrationStatus status, Pageable pageable) {

        Page<Registration> page;

        // 상태 필터 없을 시 전체 목록 조회, 있을 시 해당 상태의 목록만 조회
        if (status == null) {
            page = registrationRepository.findByUser_Id(userId, pageable);
        } else {
            page = registrationRepository.findByUser_IdAndStatus(userId, status, pageable);
        }

        return MyRegistrationRes.from(page);
    }

    @Transactional(readOnly = true)
    public RegistrationOverviewRes getMarathonParticipants(Long organizerId, Long marathonId) {

        // 마라톤 존재 여부 검증
        Marathon marathon = marathonRepository.findById(marathonId)
                .orElseThrow(() -> new CustomException(ErrorCode.MARATHON_NOT_FOUND));

        // 주최자가 개최한 마라톤인지 검증
        if (!marathon.getOrganizer().getId().equals(organizerId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 해당 마라톤의 접수 목록 조회 후 참가자 정보 DTO로 변환
        List<RegistrationParticipantRes> participants = registrationRepository
                .findAllByMarathon_IdOrderByAppliedAtDesc(marathonId)
                .stream()
                .map(RegistrationParticipantRes::from)
                .toList();

        // 해당 마라톤의 코스 목록 조회 후 코스별 접수 현황 DTO로 변환
        List<CourseRegistrationStatusRes> courseStatuses = courseRepository
                .findAllByMarathon_IdOrderByIdAsc(marathonId)
                .stream()
                .map(CourseRegistrationStatusRes::from)
                .toList();

        // "참가자 목록 + 코스별 접수 현황"을 하나의 응답으로 처리
        return RegistrationOverviewRes.of(participants, courseStatuses);
    }

}
