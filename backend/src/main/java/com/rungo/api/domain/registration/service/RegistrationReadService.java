package com.rungo.api.domain.registration.service;

import com.rungo.api.domain.marathon.course.repository.CourseRepository;
import com.rungo.api.domain.marathon.marathon.repository.MarathonRepository;
import com.rungo.api.domain.registration.dto.CourseRegistrationStatusRes;
import com.rungo.api.domain.registration.dto.MyRegistrationRes;
import com.rungo.api.domain.registration.dto.RegistrationOverviewRes;
import com.rungo.api.domain.registration.dto.RegistrationParticipantRes;
import com.rungo.api.domain.registration.repository.RegistrationRepository;
import com.rungo.api.global.exception.CustomException;
import com.rungo.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationReadService {

    private final RegistrationRepository registrationRepository;
    private final MarathonRepository marathonRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<MyRegistrationRes> getMyRegistrations(Long userId) {

        // 사용자의 id로 DB 조회 후 신청일 기준 내림차순 정렬
        return registrationRepository.findAllByUser_IdOrderByAppliedAtDesc(userId)
                .stream()
                .map(MyRegistrationRes::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RegistrationOverviewRes getMarathonParticipants(Long organizerId, Long marathonId) {

        // 주최자의 개최한 마라톤인지 검증
        marathonRepository.findByIdAndOrganizer_Id(marathonId, organizerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MARATHON_NOT_FOUND));

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
