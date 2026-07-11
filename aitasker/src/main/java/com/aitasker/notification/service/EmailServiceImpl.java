package com.aitasker.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import java.lang.reflect.Method;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final ApplicationContext context;

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
    public void sendPaymentNotification(String recipientEmail, String projectTitle, double amount, String status) {
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
        Object mailSender = null;
        try {
            // Dynamic lookup of JavaMailSender bean if it exists in ApplicationContext
            mailSender = context.getBean("mailSender");
        } catch (Exception e) {
            // Ignored - mailSender bean not present in context
        }

        if (mailSender != null) {
            try {
                // Dynamically invoke JavaMailSender to send emails
                Method createMimeMessageMethod = mailSender.getClass().getMethod("createMimeMessage");
                Object mimeMessage = createMimeMessageMethod.invoke(mailSender);

                // Get MimeMessageHelper class dynamically
                Class<?> helperClass = Class.forName("org.springframework.mail.javamail.MimeMessageHelper");
                // Find helper constructor: Helper(MimeMessage, boolean multipart, String encoding)
                Class<?> mimeMessageInterface = Class.forName("jakarta.mail.internet.MimeMessage");
                Object helper = helperClass.getConstructor(mimeMessageInterface, boolean.class, String.class)
                        .newInstance(mimeMessage, true, "UTF-8");

                // Invoke setTo, setSubject, setText dynamically
                Method setToMethod = helperClass.getMethod("setTo", String.class);
                setToMethod.invoke(helper, to);

                Method setSubjectMethod = helperClass.getMethod("setSubject", String.class);
                setSubjectMethod.invoke(helper, subject);

                Method setTextMethod = helperClass.getMethod("setText", String.class, boolean.class);
                setTextMethod.invoke(helper, content, false);

                // Invoke mailSender.send(mimeMessage)
                Method sendMethod = mailSender.getClass().getMethod("send", mimeMessageInterface);
                sendMethod.invoke(mailSender, mimeMessage);

                log.info("Real email sent successfully via reflection to {}", to);
            } catch (Exception e) {
                log.error("Failed to send real email via reflection helper to {}: {}", to, e.getMessage());
                logConsoleEmail(to, subject, content);
            }
        } else {
            logConsoleEmail(to, subject, content);
        }
    }

    private void logConsoleEmail(String to, String subject, String content) {
        log.info("\n" +
                        "============================================================\n" +
                        "✉️  MOCK EMAIL SENT SUCCESSFULLY (No JavaMailSender configured)\n" +
                        "------------------------------------------------------------\n" +
                        "TO     : {}\n" +
                        "SUBJECT: {}\n" +
                        "CONTENT:\n" +
                        "{}\n" +
                        "============================================================\n",
                to, subject, content);
    }
}
