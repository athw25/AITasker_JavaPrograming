package com.aitasker.notification.service;

import java.math.BigDecimal;

public interface EmailService {
    void sendProposalNotification(String recipientEmail, String proposalTitle, String status);
    void sendPaymentNotification(String recipientEmail, String projectTitle, BigDecimal amount, String status);
    void sendDisputeNotification(String recipientEmail, String projectTitle, String disputeReason);
    void sendPasswordResetEmail(String recipientEmail, String resetToken);
    void sendEmail(String to, String subject, String content);
}
