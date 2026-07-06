package com.aitasker.notification.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.user.entity.User;
import com.aitasker.notification.enums.NotificationChannel;
import com.aitasker.notification.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification_preferences", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "notification_type", "notification_channel"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel notificationChannel;

    @Column(name = "enabled", columnDefinition = "BIT DEFAULT 1")
    private Boolean enabled = true;

    @Column(name = "frequency", length = 50)
    private String frequency; // IMMEDIATE, DAILY, WEEKLY, OFF

    @Column(name = "quiet_hours_start")
    private String quietHoursStart; // HH:mm format

    @Column(name = "quiet_hours_end")
    private String quietHoursEnd; // HH:mm format

    @Column(name = "do_not_disturb", columnDefinition = "BIT DEFAULT 0")
    private Boolean doNotDisturb = false;
}

