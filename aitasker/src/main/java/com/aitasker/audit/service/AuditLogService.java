package com.aitasker.audit.service;

import com.aitasker.audit.entity.AuditLog;
import com.aitasker.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(String action, String details, String actorEmail, String ipAddress) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .details(details)
                .actorEmail(actorEmail)
                .ipAddress(ipAddress != null ? ipAddress : "UNKNOWN")
                .createdAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(auditLog);
    }

    public Page<AuditLog> getLogs(String searchEmail, String searchAction, Pageable pageable) {
        if (searchEmail != null && !searchEmail.trim().isEmpty()) {
            return auditLogRepository.findByActorEmailContainingIgnoreCaseOrderByCreatedAtDesc(searchEmail.trim(), pageable);
        }
        if (searchAction != null && !searchAction.trim().isEmpty()) {
            return auditLogRepository.findByActionContainingIgnoreCaseOrderByCreatedAtDesc(searchAction.trim(), pageable);
        }
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
}
