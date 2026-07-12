package com.aitasker.security.audit.entity;

import com.aitasker.security.audit.enums.AuditAction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AuditAction action;

    // Không dùng @ManyToOne tới User: Audit Log phải tồn tại độc lập
    // ngay cả khi actor là null (vd: login thất bại với email không tồn tại).
    private Long actorId;

    private String actorEmail;

    @Column(length = 60)
    private String entityType;

    private String entityId;

    private String ipAddress;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
