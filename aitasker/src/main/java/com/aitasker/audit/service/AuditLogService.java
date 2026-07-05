package com.aitasker.audit.service;

import com.aitasker.audit.entity.AuditLog;
import com.aitasker.audit.repository.AuditLogRepository;
import com.aitasker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(User actor, String action, String description) {
        auditLogRepository.save(
                AuditLog.builder()
                        .actor(actor)
                        .action(action)
                        .description(description)
                        .build()
        );
    }
}
