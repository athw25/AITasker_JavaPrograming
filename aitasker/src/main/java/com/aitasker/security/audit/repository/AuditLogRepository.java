package com.aitasker.security.audit.repository;

import com.aitasker.security.audit.entity.AuditLog;
import com.aitasker.security.audit.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByAction(AuditAction action, Pageable pageable);

    Page<AuditLog> findByActorId(Long actorId, Pageable pageable);

    Page<AuditLog> findByActionAndActorId(AuditAction action, Long actorId, Pageable pageable);
}
