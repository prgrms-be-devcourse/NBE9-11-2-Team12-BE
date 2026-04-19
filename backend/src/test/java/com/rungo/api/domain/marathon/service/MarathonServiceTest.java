
package com.rungo.api.domain.marathon.service;

import com.rungo.api.domain.marathon.marathon.dto.create.CreateMarathonReq;
import com.rungo.api.domain.marathon.marathon.dto.create.CreateMarathonRes;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;
import com.rungo.api.domain.marathon.marathon.repository.MarathonRepository;
import com.rungo.api.domain.marathon.marathon.service.MarathonService;
import com.rungo.api.domain.notification.event.MarathonCanceledEvent;
import com.rungo.api.domain.registration.repository.RegistrationRepository;
import com.rungo.api.domain.users.entity.Users;
import com.rungo.api.domain.users.enumtype.Gender;
import com.rungo.api.domain.users.enumtype.Role;
import com.rungo.api.domain.users.repository.UserRepository;
import com.rungo.api.global.exception.CustomException;
import com.rungo.api.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MarathonServiceTest {

    @InjectMocks
    private MarathonService marathonService;

    @Mock
    private MarathonRepository marathonRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(marathonService, "minDaysBetweenStartAndEnd", 1L);
        ReflectionTestUtils.setField(marathonService, "minDaysBetweenEndAndEvent", 1L);
    }

    @Test
    @DisplayName("대회 취소 성공 시 참가자들에게 취소 알림 이벤트를 발행한다")
    void cancel_marathon_publish_event_success() {
        // given
        Users organizer = Users.builder()
                               .id(1L)
                               .role(Role.ORGANIZER)
                               .build();
        Marathon marathon = new Marathon(
                organizer,
                "서울 마라톤",
                "서울",
                LocalDate.of(2026, 10, 3),
                "poster.png",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                MarathonStatus.OPEN
        );

        ReflectionTestUtils.setField(marathon, "id", 1L);

        given(userRepository.findById(1L)).willReturn(Optional.of(organizer));
        given(marathonRepository.findById(1L)).willReturn(Optional.of(marathon));
        given(registrationRepository.findParticipantEmailsByMarathonId(1L))
                .willReturn(List.of("user1@test.com", "user2@test.com"));

        marathonService.cancelMarathon(1L, 1L);

        verify(eventPublisher, times(1))
                .publishEvent(any(MarathonCanceledEvent.class));
    }

    @Test

    @DisplayName("마라톤 생성 성공 - 저장과 응답 반환 및 코스 정규화가 정상 동작한다")

    void create_success() {

        Long organizerId = 1L;

        Users organizer = createUser(organizerId, "주최자", Role.ORGANIZER);

        CreateMarathonReq request = new CreateMarathonReq(

                "서울 마라톤",

                "서울",

                LocalDate.of(2026, 10, 3),

                "poster.png",

                LocalDateTime.of(2026, 8, 1, 9, 0),

                LocalDateTime.of(2026, 8, 31, 18, 0),

                List.of(

                        new CreateMarathonReq.CreateCourseItemReq("5k", new BigDecimal("30000"), 100),

                        new CreateMarathonReq.CreateCourseItemReq("10K", new BigDecimal("50000"), 200)

                )

        );

        given(userRepository.findById(organizerId)).willReturn(Optional.of(organizer));

        given(marathonRepository.save(any(Marathon.class))).willAnswer(invocation -> {

            Marathon saved = invocation.getArgument(0);

            ReflectionTestUtils.setField(saved, "id", 10L);

            return saved;

        });

        CreateMarathonRes result = marathonService.createMarathon(organizerId, request);

        ArgumentCaptor<Marathon> marathonCaptor = ArgumentCaptor.forClass(Marathon.class);

        verify(marathonRepository, times(1)).save(marathonCaptor.capture());

        Marathon capturedMarathon = marathonCaptor.getValue();

        assertSame(organizer, capturedMarathon.getOrganizer());

        assertEquals("서울 마라톤", capturedMarathon.getTitle());

        assertEquals("서울", capturedMarathon.getRegion());

        assertEquals(LocalDate.of(2026, 10, 3), capturedMarathon.getEventDate());

        assertEquals("poster.png", capturedMarathon.getPosterImageUrl());

        assertEquals(LocalDateTime.of(2026, 8, 1, 9, 0), capturedMarathon.getRegistrationStartAt());

        assertEquals(LocalDateTime.of(2026, 8, 31, 18, 0), capturedMarathon.getRegistrationEndAt());

        assertEquals(MarathonStatus.OPEN, capturedMarathon.getStatus());

        assertEquals(2, capturedMarathon.getCourses().size());

        assertEquals("5K", capturedMarathon.getCourses().get(0).getCourseType());

        assertEquals(new BigDecimal("30000"), capturedMarathon.getCourses().get(0).getPrice());

        assertEquals(100, capturedMarathon.getCourses().get(0).getCapacity());

        assertEquals(0, capturedMarathon.getCourses().get(0).getCurrentCount());

        assertEquals("10K", capturedMarathon.getCourses().get(1).getCourseType());

        assertEquals(new BigDecimal("50000"), capturedMarathon.getCourses().get(1).getPrice());

        assertEquals(200, capturedMarathon.getCourses().get(1).getCapacity());

        assertEquals(0, capturedMarathon.getCourses().get(1).getCurrentCount());

        assertNotNull(result);

        assertEquals(10L, result.id());

        assertEquals("서울 마라톤", result.title());

        assertEquals("서울", result.region());

        assertEquals(LocalDate.of(2026, 10, 3), result.eventDate());

        assertEquals("poster.png", result.posterImageUrl());

        assertEquals(LocalDateTime.of(2026, 8, 1, 9, 0), result.registrationStartAt());

        assertEquals(LocalDateTime.of(2026, 8, 31, 18, 0), result.registrationEndAt());

        assertEquals(MarathonStatus.OPEN, result.status());

        assertEquals(2, result.courses().size());

        assertEquals("5K", result.courses().get(0).courseType());

        assertEquals("10K", result.courses().get(1).courseType());

    }

    @Test

    @DisplayName("마라톤 생성 실패 - 사용자가 없으면 USER_NOT_FOUND 예외가 발생한다")

    void create_fail_user_not_found() {

        Long organizerId = 1L;

        CreateMarathonReq request = new CreateMarathonReq(

                "서울 마라톤",

                "서울",

                LocalDate.of(2026, 10, 3),

                "poster.png",

                LocalDateTime.of(2026, 8, 1, 9, 0),

                LocalDateTime.of(2026, 8, 31, 18, 0),

                List.of(

                        new CreateMarathonReq.CreateCourseItemReq("5K", new BigDecimal("30000"), 100)

                )

        );

        given(userRepository.findById(organizerId)).willReturn(Optional.empty());

        CustomException exception = assertThrows(

                CustomException.class,

                () -> marathonService.createMarathon(organizerId, request)

        );

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

    }

    @Test

    @DisplayName("마라톤 생성 실패 - 주최자 권한이 아니면 FORBIDDEN 예외가 발생한다")

    void create_fail_not_organizer() {

        Long organizerId = 1L;

        Users participant = createUser(organizerId, "참가자", Role.PARTICIPANT);

        CreateMarathonReq request = new CreateMarathonReq(

                "서울 마라톤",

                "서울",

                LocalDate.of(2026, 10, 3),

                "poster.png",

                LocalDateTime.of(2026, 8, 1, 9, 0),

                LocalDateTime.of(2026, 8, 31, 18, 0),

                List.of(

                        new CreateMarathonReq.CreateCourseItemReq("5K", new BigDecimal("30000"), 100)

                )

        );

        given(userRepository.findById(organizerId)).willReturn(Optional.of(participant));

        CustomException exception = assertThrows(

                CustomException.class,

                () -> marathonService.createMarathon(organizerId, request)

        );

        assertEquals(ErrorCode.FORBIDDEN, exception.getErrorCode());

    }

    @Test

    @DisplayName("마라톤 생성 실패 - 접수 시작일이 종료일보다 늦으면 INVALID_INPUT_VALUE 예외가 발생한다")

    void create_fail_registration_period_invalid() {

        Long organizerId = 1L;

        Users organizer = createUser(organizerId, "주최자", Role.ORGANIZER);

        CreateMarathonReq request = new CreateMarathonReq(

                "서울 마라톤",

                "서울",

                LocalDate.of(2026, 10, 3),

                "poster.png",

                LocalDateTime.of(2026, 9, 1, 9, 0),

                LocalDateTime.of(2026, 8, 31, 18, 0),

                List.of(

                        new CreateMarathonReq.CreateCourseItemReq("5K", new BigDecimal("30000"), 100)

                )

        );

        given(userRepository.findById(organizerId)).willReturn(Optional.of(organizer));

        CustomException exception = assertThrows(

                CustomException.class,

                () -> marathonService.createMarathon(organizerId, request)

        );

        assertEquals(ErrorCode.INVALID_INPUT_VALUE, exception.getErrorCode());

    }

    @Test

    @DisplayName("마라톤 생성 실패 - 개최일이 접수 종료일보다 이르면 INVALID_INPUT_VALUE 예외가 발생한다")

    void create_fail_event_date_invalid() {

        Long organizerId = 1L;

        Users organizer = createUser(organizerId, "주최자", Role.ORGANIZER);

        CreateMarathonReq request = new CreateMarathonReq(

                "서울 마라톤",

                "서울",

                LocalDate.of(2026, 8, 20),

                "poster.png",

                LocalDateTime.of(2026, 8, 1, 9, 0),

                LocalDateTime.of(2026, 8, 31, 18, 0),

                List.of(

                        new CreateMarathonReq.CreateCourseItemReq("5K", new BigDecimal("30000"), 100)

                )

        );

        given(userRepository.findById(organizerId)).willReturn(Optional.of(organizer));

        CustomException exception = assertThrows(

                CustomException.class,

                () -> marathonService.createMarathon(organizerId, request)

        );

        assertEquals(ErrorCode.INVALID_INPUT_VALUE, exception.getErrorCode());

    }

    @Test

    @DisplayName("마라톤 생성 실패 - 코스 타입이 정규화 후 중복되면 INVALID_INPUT_VALUE 예외가 발생한다")

    void create_fail_duplicate_course_type() {

        Long organizerId = 1L;

        Users organizer = createUser(organizerId, "주최자", Role.ORGANIZER);

        CreateMarathonReq request = new CreateMarathonReq(

                "서울 마라톤",

                "서울",

                LocalDate.of(2026, 10, 3),

                "poster.png",

                LocalDateTime.of(2026, 8, 1, 9, 0),

                LocalDateTime.of(2026, 8, 31, 18, 0),

                List.of(

                        new CreateMarathonReq.CreateCourseItemReq("5k", new BigDecimal("30000"), 100),

                        new CreateMarathonReq.CreateCourseItemReq(" 5K ", new BigDecimal("50000"), 200)

                )

        );

        given(userRepository.findById(organizerId)).willReturn(Optional.of(organizer));

        CustomException exception = assertThrows(

                CustomException.class,

                () -> marathonService.createMarathon(organizerId, request)

        );

        assertEquals(ErrorCode.INVALID_INPUT_VALUE, exception.getErrorCode());

    }

    private Users createUser(Long id, String name, Role role) {

        return Users.builder()

                .id(id)

                .email("test@test.com")

                .password("encoded-password")

                .name(name)

                .phoneNumber("010-1111-2222")

                .role(role)

                .gender(Gender.MALE)

                .birth(LocalDate.of(2000, 1, 1))

                .build();

    }
    @Test
    @DisplayName("마라톤 생성 실패 - 접수 시작일과 종료일 간격이 1일 미만이면 예외 발생")
    void create_fail_start_end_interval() {

        Long organizerId = 1L;

        Users organizer = createUser(organizerId, "주최자", Role.ORGANIZER);

        given(userRepository.findById(1L)).willReturn(Optional.of(organizer));

        LocalDateTime start = LocalDateTime.of(2026, 8, 1, 10, 0);

        LocalDateTime end = LocalDateTime.of(2026, 8, 1, 15, 0); // 같은 날 → 1일 미만

        CreateMarathonReq request = new CreateMarathonReq(

                "서울 마라톤",

                "서울",

                LocalDate.of(2026, 8, 5),

                "poster.png",

                LocalDateTime.of(2026, 8, 1, 10, 0),

                LocalDateTime.of(2026, 8, 1, 15, 0), // 최소 1일 미만

                List.of(

                        new CreateMarathonReq.CreateCourseItemReq(

                                "10K",

                                BigDecimal.valueOf(30000),

                                100

                        )

                )

        );

        CustomException exception = assertThrows(

                CustomException.class,

                () -> marathonService.createMarathon(1L, request)

        );

        assertEquals(ErrorCode.INVALID_INPUT_VALUE, exception.getErrorCode());

    }

    @Test
    @DisplayName("마라톤 생성 실패 - 접수 종료일과 대회일 간격이 1일 미만이면 예외 발생")
    void create_fail_end_event_interval() {

        Long organizerId = 1L;

        Users organizer = createUser(organizerId, "주최자", Role.ORGANIZER);

        given(userRepository.findById(1L)).willReturn(Optional.of(organizer));

        LocalDateTime start = LocalDateTime.of(2026, 8, 1, 10, 0);

        LocalDateTime end = LocalDateTime.of(2026, 8, 2, 10, 0);

        LocalDate eventDate = LocalDate.of(2026, 8, 2); // 종료일과 같은 날

        CreateMarathonReq request = new CreateMarathonReq(

                "서울 마라톤",

                "서울",

                LocalDate.of(2026, 8, 4),

                "poster.png",

                LocalDateTime.of(2026, 8, 1, 10, 0),

                LocalDateTime.of(2026, 8, 4, 15, 0),

                List.of(

                        new CreateMarathonReq.CreateCourseItemReq(

                                "10K",

                                BigDecimal.valueOf(30000),

                                100

                        )

                )

        );

        CustomException exception = assertThrows(

                CustomException.class,

                () -> marathonService.createMarathon(1L, request)

        );

        assertEquals(ErrorCode.INVALID_INPUT_VALUE, exception.getErrorCode());

    }
}