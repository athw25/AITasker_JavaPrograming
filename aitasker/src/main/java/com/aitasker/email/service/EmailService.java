package com.aitasker.email.service;

import com.aitasker.email.config.EmailProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;

    @Async
    @Transactional
    public void sendProposalNotification(String recipientEmail, String recipientName,
                                         String projectTitle, String proposalAmount,
                                         String proposalLink) {
        String subject = "New Proposal Received - " + projectTitle;
        String body = buildProposalEmailBody(recipientName, projectTitle, proposalAmount, proposalLink);
        sendHtmlEmail(recipientEmail, subject, body);
    }

    @Async
    @Transactional
    public void sendPaymentNotification(String recipientEmail, String recipientName,
                                        String paymentType, String amount,
                                        String transactionId, String status) {
        String subject = "Payment " + status + " - Transaction ID: " + transactionId;
        String body = buildPaymentEmailBody(recipientName, paymentType, amount, transactionId, status);
        sendHtmlEmail(recipientEmail, subject, body);
    }

    @Async
    @Transactional
    public void sendDisputeNotification(String recipientEmail, String recipientName,
                                        String projectTitle, String disputeReason,
                                        String disputeStatus, String disputeLink) {
        String subject = "Dispute " + disputeStatus + " - " + projectTitle;
        String body = buildDisputeEmailBody(recipientName, projectTitle, disputeReason, disputeStatus, disputeLink);
        sendHtmlEmail(recipientEmail, subject, body);
    }

    @Async
    @Transactional
    public void sendPasswordResetEmail(String recipientEmail, String recipientName,
                                       String resetLink, String expirationTime) {
        String subject = "Password Reset Request - AITasker";
        String body = buildPasswordResetEmailBody(recipientName, resetLink, expirationTime);
        sendHtmlEmail(recipientEmail, subject, body);
    }

    @Async
    private void sendHtmlEmail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(emailProperties.getFrom(), emailProperties.getFromName());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(mimeMessage);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Email sending failed", e);
        }
    }

    @Async
    private void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailProperties.getFrom());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Simple email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send simple email to: {}", to, e);
            throw new RuntimeException("Email sending failed", e);
        }
    }

    private String buildProposalEmailBody(String recipientName, String projectTitle,
                                          String proposalAmount, String proposalLink) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
                "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">" +
                "<h2>New Proposal Received</h2>" +
                "<p>Hello " + recipientName + ",</p>" +
                "<p>You have received a new proposal for your project:</p>" +
                "<div style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px;\">" +
                "<p><strong>Project:</strong> " + projectTitle + "</p>" +
                "<p><strong>Proposal Amount:</strong> " + proposalAmount + "</p>" +
                "</div>" +
                "<p><a href=\"" + proposalLink + "\" style=\"background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;\">View Proposal</a></p>" +
                "<p>Best regards,<br>AITasker Team</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildPaymentEmailBody(String recipientName, String paymentType,
                                         String amount, String transactionId, String status) {
        String statusColor = "SUCCESS".equalsIgnoreCase(status) ? "#28a745" : "#dc3545";
        return "<!DOCTYPE html>" +
                "<html>" +
                "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
                "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">" +
                "<h2>Payment " + status + "</h2>" +
                "<p>Hello " + recipientName + ",</p>" +
                "<p>Your payment has been processed:</p>" +
                "<div style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px;\">" +
                "<p><strong>Payment Type:</strong> " + paymentType + "</p>" +
                "<p><strong>Amount:</strong> " + amount + "</p>" +
                "<p><strong>Transaction ID:</strong> " + transactionId + "</p>" +
                "<p><strong style=\"color: " + statusColor + ";\">Status: " + status + "</strong></p>" +
                "</div>" +
                "<p>If you have any questions, please contact our support team.</p>" +
                "<p>Best regards,<br>AITasker Team</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildDisputeEmailBody(String recipientName, String projectTitle,
                                         String disputeReason, String disputeStatus, String disputeLink) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
                "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">" +
                "<h2>Dispute " + disputeStatus + "</h2>" +
                "<p>Hello " + recipientName + ",</p>" +
                "<p>A dispute has been " + disputeStatus + " for your project:</p>" +
                "<div style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px;\">" +
                "<p><strong>Project:</strong> " + projectTitle + "</p>" +
                "<p><strong>Reason:</strong> " + disputeReason + "</p>" +
                "<p><strong>Status:</strong> " + disputeStatus + "</p>" +
                "</div>" +
                "<p><a href=\"" + disputeLink + "\" style=\"background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;\">View Dispute Details</a></p>" +
                "<p>Best regards,<br>AITasker Team</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildPasswordResetEmailBody(String recipientName, String resetLink, String expirationTime) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
                "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">" +
                "<h2>Password Reset Request</h2>" +
                "<p>Hello " + recipientName + ",</p>" +
                "<p>You have requested to reset your password. Click the link below to proceed:</p>" +
                "<p><a href=\"" + resetLink + "\" style=\"background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;\">Reset Password</a></p>" +
                "<p><strong>Important:</strong> This link will expire in " + expirationTime + " minutes.</p>" +
                "<p>If you did not request this password reset, please ignore this email.</p>" +
                "<p>Best regards,<br>AITasker Team</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}

