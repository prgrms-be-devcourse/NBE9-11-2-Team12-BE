package com.rungo.api.domain.registration.service;

import com.rungo.api.domain.marathon.course.entity.Course;
import com.rungo.api.domain.marathon.course.repository.CourseRepository;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.repository.MarathonRepository;
import com.rungo.api.domain.registration.dto.MyRegistrationRes;
import com.rungo.api.domain.registration.entity.Registration;
import com.rungo.api.domain.registration.entity.RegistrationCancelHistory;
import com.rungo.api.domain.registration.enumtype.MyRegistrationStatusFilter;
import com.rungo.api.domain.registration.repository.RegistrationCancelHistoryRepository;
import com.rungo.api.domain.registration.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationReadService {

    private final RegistrationRepository registrationRepository;
    private final RegistrationCancelHistoryRepository registrationCancelHistoryRepository;
    private final MarathonRepository marathonRepository;
    private final CourseRepository courseRepository;

    // status 필터에 따른 내 접수 목록 조회
    // ACTIVE   : 정상 접수인 상태 (취소 되지 않은 모든 접수)
    // CANCELED : 취소된 접수 상태
    @Transactional(readOnly = true)
    public MyRegistrationRes getMyRegistrations(Long userId, MyRegistrationStatusFilter status, Pageable pageable) {

        if (status == MyRegistrationStatusFilter.CANCELED) {
            return getCanceledRegistrations(userId, pageable);
        }

        return getActiveRegistrations(userId, pageable);

    }

    // ACTIVE status 접수
    private MyRegistrationRes getActiveRegistrations(Long userId, Pageable pageable) {

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(
                        Sort.Order.desc("appliedAt"),
                        Sort.Order.desc("id")
                )
        );

        Page<Registration> page = registrationRepository.findByUser_Id(userId, sortedPageable);

        return MyRegistrationRes.fromActive(page);

    }

    // CANCEL status 접수
    private MyRegistrationRes getCanceledRegistrations(Long userId, Pageable pageable) {

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(
                        Sort.Order.desc("canceledAt"),
                        Sort.Order.desc("id")
                )
        );

        // 1. 취소 이력 페이지 조회
        Page<RegistrationCancelHistory> page = registrationCancelHistoryRepository.findByUserId(userId, sortedPageable);

        // 2. 마라톤/코스 정보 재조회 위한 id 추출
        List<Long> marathonIds = page.getContent().stream()
                .map(RegistrationCancelHistory::getMarathonId)
                .distinct()
                .toList();

        List<Long> courseIds = page.getContent().stream()
                .map(RegistrationCancelHistory::getCourseId)
                .distinct()
                .toList();

        // 3. 각각 한 번에 조회 후 Map으로 변환
        Map<Long, Marathon> marathonMap = marathonRepository.findAllById(marathonIds).stream()
                .collect(Collectors.toMap(Marathon::getId, Function.identity()));

        Map<Long, Course> courseMap = courseRepository.findAllById(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));

        // 4. 취소 이력 + 마라톤/코스 정보 결과를 합친 DTO 반환
        return MyRegistrationRes.fromCanceled(page, marathonMap, courseMap);

    }
}
