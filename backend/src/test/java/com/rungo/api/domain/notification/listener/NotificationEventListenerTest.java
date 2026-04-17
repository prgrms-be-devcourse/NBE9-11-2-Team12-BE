package com.rungo.api.domain.notification.listener;

import com.rungo.api.domain.notification.event.MarathonCanceledEvent;
import com.rungo.api.domain.notification.event.RegistrationCompletedEvent;
import com.rungo.api.global.infrastructure.mail.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationEventListenerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationEventListener notificationEventListener;

    @Test
    @DisplayName("접수 완료 이벤트 발생 시 이메일 발송 메서드가 호출된다")
    void handleRegistrationCompleted_test() {
        RegistrationCompletedEvent event = new RegistrationCompletedEvent("test@gmail.com", "서울 마라톤", "10km");

        notificationEventListener.handleRegistrationCompleted(event);

        verify(emailService, times(1)).sendEmail(eq("test@gmail.com"), anyString(), anyString());
    }

    @Test
    @DisplayName("대회 취소 이벤트 발생 시 참가자 수만큼 이메일 발송 메서드가 호출된다")
    void handleMarathonCanceled_test() {
        List<String> emails = List.of("user1@gmail.com", "user2@gmail.com");
        MarathonCanceledEvent event = new MarathonCanceledEvent("서울 마라톤", emails);

        notificationEventListener.handleMarathonCanceled(event);

        verify(emailService, times(1)).sendEmail(eq("user1@gmail.com"), anyString(), anyString());
        verify(emailService, times(1)).sendEmail(eq("user2@gmail.com"), anyString(), anyString());
    }
}