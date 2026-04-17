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
        // given
        doThrow(new RuntimeException("SMTP 서버 강제 다운"))
                .when(emailService).sendEmail(anyString(), anyString(), anyString());

        Users organizer = userRepository.save(
                Users.builder()
                     .email("organizer@test.com")
                     .password("1234")
                     .name("주최자")
                     .phoneNumber("010-1111-1111")
                     .role(Role.ORGANIZER)
                     .gender(Gender.MALE)
                     .birth(LocalDate.of(1990, 1, 1))
                     .build()
        );

        Users participant = userRepository.save(
                Users.builder()
                     .email("participant@test.com")
                     .password("1234")
                     .name("참가자")
                     .phoneNumber("010-2222-2222")
                     .role(Role.PARTICIPANT)
                     .gender(Gender.MALE)
                     .birth(LocalDate.of(2000, 1, 1))
                     .build()
        );

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
        marathonRepository.save(marathon); // cascade로 course까지 자동 저장

        CreateRegistrationReq req = new CreateRegistrationReq(
                course.getId(),
                "12345",
                "서울시 강남구",
                "101동",
                "L",
                true
        );

        CreateRegistrationRes res = registrationCommandService.create(participant.getId(), req);

        assertThat(res).isNotNull();
        assertThat(res.registrationId()).isNotNull();

        // 실제 DB에 데이터가 저장(Commit)되었는지 검증
        assertThat(registrationRepository.findById(res.registrationId())).isPresent();

        // 정원이 정상적으로 1명 늘었는지 검증
        Course savedCourse = courseRepository.findById(course.getId()).orElseThrow();
        assertThat(savedCourse.getCurrentCount()).isEqualTo(1);

        // 에러가 발생해도 이메일 발송 시도는 이루어졌는지 검증
        verify(emailService, timeout(2000).atLeastOnce())
                .sendEmail(anyString(), anyString(), anyString());
    }
}