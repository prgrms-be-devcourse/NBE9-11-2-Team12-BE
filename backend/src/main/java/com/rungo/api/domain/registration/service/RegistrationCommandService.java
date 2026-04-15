package com.rungo.api.domain.registration.service;

import com.rungo.api.domain.marathon.course.entity.Course;
import com.rungo.api.domain.marathon.course.repository.CourseRepository;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;
import com.rungo.api.domain.registration.dto.CreateRegistrationReq;
import com.rungo.api.domain.registration.dto.CreateRegistrationRes;
import com.rungo.api.domain.registration.entity.Registration;
import com.rungo.api.domain.registration.repository.RegistrationRepository;
import com.rungo.api.domain.users.entity.Users;
import com.rungo.api.domain.users.repository.UserRepository;
import com.rungo.api.global.exception.CustomException;
import com.rungo.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationCommandService {

    private final RegistrationRepository registrationRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CreateRegistrationRes create(Long userId, CreateRegistrationReq request) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));
        Marathon marathon = course.getMarathon();
        LocalDateTime now = LocalDateTime.now();

        // 필수 약관 미동의
        if (!request.agreedTerms()) {
            throw new CustomException(ErrorCode.REGISTRATION_TERMS_REQUIRED);
        }
        // 접수 기간이 아니면 생성할 수 없다.
        if (now.isBefore(marathon.getRegistrationStartAt()) || now.isAfter(marathon.getRegistrationEndAt())) {
            throw new CustomException(ErrorCode.REGISTRATION_PERIOD_INVALID);
        }
        // 모집 중인 대회만 접수 가능하다.
        if (marathon.getStatus() != MarathonStatus.OPEN) {
            throw new CustomException(ErrorCode.MARATHON_NOT_OPEN);
        }
        // 코스 정원이 가득 찼으면 접수를 막는다.
        if (course.isFull()) {
            throw new CustomException(ErrorCode.CAPACITY_FULL);
        }

        Registration registration = Registration.create(
                user,
                course,
                marathon,
                request.snapZipCode(),
                request.snapAddress(),
                request.snapDetail(),
                request.tSize(),
                request.agreedTerms()
        );

        course.increaseCurrentCount();

        Registration savedRegistration = registrationRepository.save(registration);

        return CreateRegistrationRes.from(savedRegistration);
    }

    public void cancel(Long userId, Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new CustomException(ErrorCode.REGISTRATION_NOT_FOUND));

        // 본인 신청 건만 취소할 수 있다.
        if (!registration.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        registration.getCourse().decreaseCurrentCount();
        registrationRepository.delete(registration);
    }
}
