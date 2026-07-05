package com.aitasker.security.integration;

import com.aitasker.audit.service.AuditLogService;
import com.aitasker.security.ratelimit.LoginAttemptService;
import com.aitasker.notification.service.NotificationPreferenceService;
import com.aitasker.notification.enums.NotificationChannel;
import com.aitasker.notification.enums.NotificationType;
import com.aitasker.email.service.EmailService;
import com.aitasker.security.file.FileSecurityValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityIntegrationService {

    private final AuditLogService auditLogService;
    private final LoginAttemptService loginAttemptService;
    private final NotificationPreferenceService notificationPreferenceService;
    private final EmailService emailService;
    private final FileSecurityValidator fileSecurityValidator;
    private final HttpServletRequest httpServletRequest;

    public boolean validateLoginSecurity(String email) {
        if (loginAttemptService.isAccountLocked(email)) {
            String ipAddress = auditLogService.getClientIp(httpServletRequest);
            String userAgent = httpServletRequest.getHeader("User-Agent");
            auditLogService.logLogin(null, email, ipAddress, userAgent, "LOCKED");
            return false;
        }
        return true;
    }

    public void recordSuccessfulLogin(Long userId, String userEmail) {
        loginAttemptService.loginSucceeded(userEmail);
        String ipAddress = auditLogService.getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        auditLogService.logLogin(userId, userEmail, ipAddress, userAgent, "SUCCESS");
        log.info("Successful login recorded for: {}", userEmail);
    }

    public void recordFailedLogin(String userEmail) {
        loginAttemptService.loginFailed(userEmail);
        String ipAddress = auditLogService.getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        auditLogService.logLogin(null, userEmail, ipAddress, userAgent, "FAILED");
        log.warn("Failed login recorded for: {}", userEmail);
    }

    public boolean shouldSendNotification(Long userId, NotificationType notificationType, NotificationChannel notificationChannel) {
        if (!notificationPreferenceService.isNotificationEnabled(userId, notificationType, notificationChannel)) {
            return false;
        }

        if (notificationPreferenceService.isQuietHours(userId, notificationChannel)) {
            log.info("Notification skipped due to quiet hours for user: {}", userId);
            return false;
        }

        return true;
    }

    public void sendEmailWithPreferenceCheck(Long userId, NotificationType notificationType,
                                           String recipientEmail, String recipientName,
                                           Runnable emailSender) {
        if (shouldSendNotification(userId, notificationType, NotificationChannel.EMAIL)) {
            try {
                emailSender.run();
                log.info("Email sent for notification type: {}", notificationType);
            } catch (Exception e) {
                log.error("Failed to send email for notification type: {}", notificationType, e);
            }
        }
    }

    public boolean validateFileUpload(MultipartFile file) {
        if (!fileSecurityValidator.validateFile(file)) {
            log.warn("File validation failed for: {}", file.getOriginalFilename());
            String ipAddress = auditLogService.getClientIp(httpServletRequest);
            String userAgent = httpServletRequest.getHeader("User-Agent");
            auditLogService.logAction("FILE_UPLOAD", "FILE", null, null, null,
                    "File validation failed: " + file.getOriginalFilename(),
                    "Invalid file type/size", ipAddress, userAgent, "FAILED");
            return false;
        }

        String ipAddress = auditLogService.getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        auditLogService.logAction("FILE_UPLOAD", "FILE", null, null, null,
                "File uploaded: " + file.getOriginalFilename(),
                null, ipAddress, userAgent, "SUCCESS");
        return true;
    }

    public void logPaymentAction(Long userId, String userEmail, Long paymentId,
                                String description, String details, String status) {
        String ipAddress = auditLogService.getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        auditLogService.logPayment(userId, userEmail, paymentId, description, details, ipAddress, userAgent, status);

        if ("SUCCESS".equalsIgnoreCase(status)) {
            try {
                if (shouldSendNotification(userId, NotificationType.PAYMENT, NotificationChannel.EMAIL)) {
                    emailService.sendPaymentNotification(userEmail, userEmail, "Payment", description, 
                            String.valueOf(paymentId), status);
                }
            } catch (Exception e) {
                log.error("Failed to send payment notification", e);
            }
        }
    }

    public void logDisputeAction(Long userId, String userEmail, Long disputeId,
                                String projectTitle, String reason, String status) {
        String ipAddress = auditLogService.getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        auditLogService.logDispute(userId, userEmail, disputeId, "Dispute: " + projectTitle, reason, ipAddress, userAgent, status);

        if ("OPENED".equalsIgnoreCase(status)) {
            try {
                if (shouldSendNotification(userId, NotificationType.DISPUTE, NotificationChannel.EMAIL)) {
                    emailService.sendDisputeNotification(userEmail, userEmail, projectTitle, reason, status,
                            "http://example.com/dispute/" + disputeId);
                }
            } catch (Exception e) {
                log.error("Failed to send dispute notification", e);
            }
        }
    }

    public void logAdminAction(Long userId, String userEmail, String actionType, String entityType,
                              Long entityId, String description, String details, String status) {
        String ipAddress = auditLogService.getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        auditLogService.logAdminAction(userId, userEmail, actionType, entityType, entityId, description, details, ipAddress, userAgent, status);
        log.info("Admin action logged: {} - {}", actionType, entityType);
    }
}
