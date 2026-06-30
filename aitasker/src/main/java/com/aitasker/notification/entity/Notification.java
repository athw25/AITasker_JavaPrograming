// Notification.java
package com.aitasker.notification.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String message;

    private boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}