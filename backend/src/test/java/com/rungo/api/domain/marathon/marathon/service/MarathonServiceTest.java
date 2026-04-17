package com.rungo.api.domain.marathon.marathon.service;

import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;
import com.rungo.api.domain.marathon.marathon.repository.MarathonRepository;
import com.rungo.api.domain.notification.event.MarathonCanceledEvent;
import com.rungo.api.domain.registration.repository.RegistrationRepository;
import com.rungo.api.domain.users.entity.Users;
import com.rungo.api.domain.users.enumtype.Role;
import com.rungo.api.domain.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
}