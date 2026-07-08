package com.aitasker.audit.repository;

import com.aitasker.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByAction(String action);
    List<AuditLog> findByActor_Id(Long actorId);
}
