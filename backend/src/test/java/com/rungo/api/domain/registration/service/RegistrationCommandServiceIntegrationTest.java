package com.rungo.api.domain.registration.service;

import com.rungo.api.domain.marathon.course.entity.Course;
import com.rungo.api.domain.marathon.course.repository.CourseRepository;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;
import com.rungo.api.domain.marathon.marathon.repository.MarathonRepository;
import com.rungo.api.domain.registration.dto.CreateRegistrationReq;
import com.rungo.api.domain.registration.dto.CreateRegistrationRes;
import com.rungo.api.domain.registration.repository.RegistrationRepository;
import com.rungo.api.domain.users.entity.Users;
import com.rungo.api.domain.users.enumtype.Gender;
import com.rungo.api.domain.users.enumtype.Role;
import com.rungo.api.domain.users.repository.UserRepository;
import com.rungo.api.global.infrastructure.mail.EmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
class RegistrationCommandServiceIntegrationTest {

    @Autowired
    private RegistrationCommandService registrationCommandService;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MarathonRepository marathonRepository;

    @Autowired
    private CourseRepository courseRepository;

    @MockitoBean
    private EmailService emailService;

    @AfterEach
    void tearDown() {
        registrationRepository.deleteAllInBatch();
        courseRepository.deleteAllInBatch();
        marathonRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("이메일 발송 실패가 발생해도 참가 접수 데이터는 정상 저장된다")
    void email_exception_isolation_test() {
        doThrow(new RuntimeException("SMTP 서버 강제 다운"))
                .when(emailService).sendEmail(anyString(), anyString(), anyString());

        Users organizer = saveOrganizer("organizer@test.com");
        Users participant = saveParticipant("participant@test.com");
        Course course = saveCourseWithMarathon(organizer);

        CreateRegistrationReq req = createRegistrationReq(course.getId());

        CreateRegistrationRes res = registrationCommandService.create(participant.getId(), req);

        assertThat(res).isNotNull();
        assertThat(res.registrationId()).isNotNull();
        assertThat(registrationRepository.findById(res.registrationId())).isPresent();

        Course savedCourse = courseRepository.findById(course.getId()).orElseThrow();
        assertThat(savedCourse.getCurrentCount()).isEqualTo(1);

        verify(emailService, timeout(2000).atLeastOnce())
                .sendEmail(eq("participant@test.com"), anyString(), anyString());
    }

    @Test
    @DisplayName("참가 접수 성공 시 이메일이 비동기로 발송되고 접수 데이터가 저장된다")
    void registration_success_email_send_test() {
        Users organizer = saveOrganizer("organizer-success@test.com");
        Users participant = saveParticipant("participant-success@test.com");
        Course course = saveCourseWithMarathon(organizer);

        CreateRegistrationReq req = createRegistrationReq(course.getId());

        CreateRegistrationRes res = registrationCommandService.create(participant.getId(), req);

        assertThat(res).isNotNull();
        assertThat(res.registrationId()).isNotNull();
        assertThat(registrationRepository.findById(res.registrationId())).isPresent();

        Course savedCourse = courseRepository.findById(course.getId()).orElseThrow();
        assertThat(savedCourse.getCurrentCount()).isEqualTo(1);

        verify(emailService, timeout(2000).times(1))
                .sendEmail(eq("participant-success@test.com"), anyString(), anyString());
    }

    private Users saveOrganizer(String email) {
        return userRepository.save(
                Users.builder()
                     .email(email)
                     .password("1234")
                     .name("주최자")
                     .phoneNumber("010-1111-1111")
                     .role(Role.ORGANIZER)
                     .gender(Gender.MALE)
                     .birth(LocalDate.of(1990, 1, 1))
                     .build()
        );
    }

    private Users saveParticipant(String email) {
        return userRepository.save(
                Users.builder()
                     .email(email)
                     .password("1234")
                     .name("참가자")
                     .phoneNumber("010-2222-2222")
                     .role(Role.PARTICIPANT)
                     .gender(Gender.MALE)
                     .birth(LocalDate.of(2000, 1, 1))
                     .build()
        );
    }

    private Course saveCourseWithMarathon(Users organizer) {
        Marathon marathon = new Marathon(
                organizer,
                "서울 마라톤",
                "서울",
                LocalDate.now().plusDays(10),
                "poster.png",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(5),
                MarathonStatus.OPEN
        );

        Course course = new Course(
                "10K",
                BigDecimal.valueOf(30000),
                100,
                0
        );

        marathon.addCourse(course);
        marathonRepository.save(marathon);

        return course;
    }

    private CreateRegistrationReq createRegistrationReq(Long courseId) {
        return new CreateRegistrationReq(
                courseId,
                "12345",
                "서울시 강남구",
                "101동",
                "L",
                true
        );
    }
}