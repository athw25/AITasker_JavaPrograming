package com.aitasker.audit.service;

import com.aitasker.audit.entity.AuditLog;
import com.aitasker.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void logAction(String actionType, String entityType, Long entityId, Long userId,
                         String userEmail, String description, String details,
                         String ipAddress, String userAgent, String status) {
        AuditLog auditLog = AuditLog.builder()
                .actionType(actionType)
                .entityType(entityType)
                .entityId(entityId)
                .userId(userId)
                .userEmail(userEmail)
                .description(description)
                .details(details)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .status(status)
                .build();
        auditLogRepository.save(auditLog);
    }

    @Transactional
    public void logLogin(Long userId, String userEmail, String ipAddress, String userAgent, String status) {
        logAction("LOGIN", "USER", userId, userId, userEmail, 
                "User login attempt", null, ipAddress, userAgent, status);
    }

    @Transactional
    public void logPayment(Long userId, String userEmail, Long paymentId, String description,
                          String details, String ipAddress, String userAgent, String status) {
        logAction("PAYMENT", "PAYMENT", paymentId, userId, userEmail, 
                description, details, ipAddress, userAgent, status);
    }

    @Transactional
    public void logDispute(Long userId, String userEmail, Long disputeId, String description,
                          String details, String ipAddress, String userAgent, String status) {
        logAction("DISPUTE", "DISPUTE", disputeId, userId, userEmail, 
                description, details, ipAddress, userAgent, status);
    }

    @Transactional
    public void logAdminAction(Long userId, String userEmail, String actionType, String entityType,
                              Long entityId, String description, String details,
                              String ipAddress, String userAgent, String status) {
        logAction("ADMIN_" + actionType, entityType, entityId, userId, userEmail, 
                description, details, ipAddress, userAgent, status);
    }

    public List<AuditLog> getAuditLogsByUser(Long userId) {
        return auditLogRepository.findByUserIdOrderByCreatedDateDesc(userId);
    }

    public List<AuditLog> getAuditLogsByActionType(String actionType) {
        return auditLogRepository.findByActionTypeOrderByCreatedDateDesc(actionType);
    }

    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByCreatedDateBetweenOrderByCreatedDateDesc(startDate, endDate);
    }

    public List<AuditLog> getAuditLogsByUserAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByUserIdAndCreatedDateBetweenOrderByCreatedDateDesc(userId, startDate, endDate);
    }

    public List<AuditLog> getAuditLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedDateDesc(entityType, entityId);
    }

    public String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
