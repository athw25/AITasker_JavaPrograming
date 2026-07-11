package com.aitasker.notification.service;

public interface EmailService {
    void sendProposalNotification(String recipientEmail, String proposalTitle, String status);
    void sendPaymentNotification(String recipientEmail, String projectTitle, double amount, String status);
    void sendDisputeNotification(String recipientEmail, String projectTitle, String disputeReason);
    void sendPasswordResetEmail(String recipientEmail, String resetToken);
    void sendEmail(String to, String subject, String content);
}
