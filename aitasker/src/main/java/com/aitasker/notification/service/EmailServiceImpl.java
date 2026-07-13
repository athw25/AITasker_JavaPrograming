package com.aitasker.notification.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    // JavaMailSender chỉ được cấu hình khi có SPRING_MAIL_HOST; dùng ObjectProvider
    // để môi trường dev/test không cấu hình SMTP vẫn khởi động được bình thường.
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Override
    public void sendProposalNotification(String recipientEmail, String proposalTitle, String status) {
        String subject = "[AITasker] Cập nhật trạng thái Proposal của bạn";
        String content = String.format(
                "Xin chào,\n\nProposal '%s' của bạn đã chuyển sang trạng thái: %s.\nVui lòng kiểm tra hệ thống để biết thêm chi tiết.",
                proposalTitle, status
        );
        sendEmail(recipientEmail, subject, content);
    }

    @Override
    public void sendPaymentNotification(String recipientEmail, String projectTitle, java.math.BigDecimal amount, String status) {
        String subject = "[AITasker] Thông báo giao dịch thanh toán";
        String content = String.format(
                "Xin chào,\n\nGiao dịch thanh toán trị giá $%.2f cho dự án '%s' của bạn đã chuyển sang trạng thái: %s.",
                amount, projectTitle, status
        );
        sendEmail(recipientEmail, subject, content);
    }

    @Override
    public void sendDisputeNotification(String recipientEmail, String projectTitle, String disputeReason) {
        String subject = "[AITasker] Cảnh báo: Tranh chấp dự án";
        String content = String.format(
                "Xin chào,\n\nDự án '%s' của bạn đã xảy ra tranh chấp với lý do: %s.\nĐội ngũ Admin đang tiến hành xem xét và xử lý.",
                projectTitle, disputeReason
        );
        sendEmail(recipientEmail, subject, content);
    }

    @Override
    public void sendPasswordResetEmail(String recipientEmail, String resetToken) {
        String subject = "[AITasker] Khôi phục mật khẩu";
        String content = String.format(
                "Xin chào,\n\nBạn nhận được email này vì đã yêu cầu khôi phục mật khẩu.\nMã khôi phục (Reset Token) của bạn là: %s\nToken này có hiệu lực trong vòng 15 phút.",
                resetToken
        );
        sendEmail(recipientEmail, subject, content);
    }

    @Override
    public void sendEmail(String to, String subject, String content) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();

        if (mailSender == null) {
            logConsoleEmail(to, subject, content);
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, false);
            mailSender.send(mimeMessage);
            log.info("Đã gửi email thành công tới {}", to);
        } catch (Exception e) {
            log.error("Gửi email thất bại tới {}: {}", to, e.getMessage());
            logConsoleEmail(to, subject, content);
        }
    }

    private void logConsoleEmail(String to, String subject, String content) {
        log.info("\n" +
                        "============================================================\n" +
                        "✉️  MOCK EMAIL (Chưa cấu hình SMTP - SPRING_MAIL_HOST)\n" +
                        "------------------------------------------------------------\n" +
                        "TO     : {}\n" +
                        "SUBJECT: {}\n" +
                        "CONTENT:\n" +
                        "{}\n" +
                        "============================================================\n",
                to, subject, content);
    }
}
