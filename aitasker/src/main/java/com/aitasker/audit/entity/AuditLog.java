package com.aitasker.audit.entity;

import com.aitasker.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends BaseEntity {

    @Column(nullable = false)
    private String action;

    @Column(name = "actor_email")
    private String actorEmail;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(length = 2000)
    private String details;
}
