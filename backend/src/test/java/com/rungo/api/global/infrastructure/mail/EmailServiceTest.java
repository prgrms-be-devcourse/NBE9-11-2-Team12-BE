package com.rungo.api.global.infrastructure.mail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    @DisplayName("실제 SMTP 서버를 통해 이메일이 정상적으로 전송되는지 확인한다")
    void real_email_send_test() {
        String to = "mwon1590@gmail.com"; // 본인 실제 이메일로 변경
        String subject = "[Rungo] 이메일 전송 API 테스트";
        String body = "EmailService 구현 성공";

        // 콘솔에 "이메일 발송 성공" 로그 찍히는지 확인
        emailService.sendEmail(to, subject, body);
    }
}