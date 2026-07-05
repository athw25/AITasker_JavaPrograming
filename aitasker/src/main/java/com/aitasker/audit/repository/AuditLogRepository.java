package com.aitasker.audit.repository;

import com.aitasker.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserIdOrderByCreatedDateDesc(Long userId);
    List<AuditLog> findByActionTypeOrderByCreatedDateDesc(String actionType);
    List<AuditLog> findByCreatedDateBetweenOrderByCreatedDateDesc(LocalDateTime startDate, LocalDateTime endDate);
    List<AuditLog> findByUserIdAndCreatedDateBetweenOrderByCreatedDateDesc(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedDateDesc(String entityType, Long entityId);
}