package com.aitasker.notification.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Async
    public void send(String to, String subject, String body) {
        if (!mailProperties.isEnabled()) {
            log.debug("Email đang tắt (app.mail.enabled=false). Bỏ qua gửi mail tới {}", to);
            return;
        }
        if (to == null || to.isBlank()) {
            log.warn("Không thể gửi email: địa chỉ người nhận trống");
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailProperties.getFrom());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {}: {}", to, subject);
        } catch (MailException e) {
            log.error("Gửi email tới {} thất bại: {}", to, e.getMessage());
        }
    }
}
