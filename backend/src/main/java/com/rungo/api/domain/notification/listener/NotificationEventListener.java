package com.rungo.api.domain.notification.listener;

import com.rungo.api.domain.notification.event.MarathonCanceledEvent;
import com.rungo.api.domain.notification.event.RegistrationCompletedEvent;
import com.rungo.api.global.infrastructure.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final EmailService emailService;

    @Async("mailExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRegistrationCompleted(RegistrationCompletedEvent event) {
        String subject = "[Rungo] " + event.marathonTitle() + " 참가 접수 완료 안내";
        String body = String.format("안녕하세요!\n\n%s 대회의 [%s] 코스 접수가 정상적으로 완료되었습니다.",
                event.marathonTitle(), event.courseName());
        emailService.sendEmail(event.email(), subject, body);
    }

    @Async("mailExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMarathonCanceled(MarathonCanceledEvent event) {
        String subject = "[Rungo] " + event.marathonTitle() + " 대회 취소 안내";
        String body = "주최측 사정으로 인해 " + event.marathonTitle() + " 대회가 취소되었습니다.\n자세한 사항은 홈페이지를 참고 바랍니다.";
        for (String email : event.participantEmails()) {
            emailService.sendEmail(email, subject, body);
        }
    }
}