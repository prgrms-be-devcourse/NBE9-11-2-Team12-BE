package com.rungo.api.global.infrastructure.mail; // 패키지 위치 수정

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /*
     단순 텍스트 이메일 발송
     @param to 수신자 이메일 주소
     @param subject 메일 제목
     @param body 메일 본문
     */
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom(fromEmail);

            mailSender.send(message);
            log.info("이메일 발송 성공: to={}", to);
        } catch (Exception e) {
            // Stacktrace 전체 로깅
            // 취소메일인지 접수메일인지 확인
            log.error("이메일 발송 실패: to={}, subject={}", to, subject, e);
        }
    }
}