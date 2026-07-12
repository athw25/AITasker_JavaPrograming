package com.aitasker.security.audit.service;

import com.aitasker.security.audit.entity.AuditLog;
import com.aitasker.security.audit.enums.AuditAction;
import com.aitasker.security.audit.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Async
    @Transactional
    public void log(AuditAction action, Long actorId, String actorEmail,
                    String entityType, String entityId, String description) {
        try {
            AuditLog entry = AuditLog.builder()
                    .action(action)
                    .actorId(actorId)
                    .actorEmail(actorEmail)
                    .entityType(entityType)
                    .entityId(entityId)
                    .description(description)
                    .ipAddress(currentClientIp())
                    .build();
            auditLogRepository.save(entry);
        } catch (Exception e) {
            log.error("Audit log failed [{}]: {}", action, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> search(AuditAction action, Long actorId, Pageable pageable) {
        if (action != null && actorId != null) {
            return auditLogRepository.findByActionAndActorId(action, actorId, pageable);
        }
        if (action != null) {
            return auditLogRepository.findByAction(action, pageable);
        }
        if (actorId != null) {
            return auditLogRepository.findByActorId(actorId, pageable);
        }
        return auditLogRepository.findAll(pageable);
    }

    private String currentClientIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;
            HttpServletRequest request = attrs.getRequest();
            String forwarded = request.getHeader("X-Forwarded-For");
            return (forwarded != null && !forwarded.isBlank())
                    ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
        } catch (Exception e) {
            return null;
        }
    }
}
