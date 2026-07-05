package com.aitasker.notification.dto;

import com.aitasker.notification.enums.NotificationChannel;
import com.aitasker.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreferenceDTO {
    private Long id;
    private Long userId;
    private NotificationType notificationType;
    private NotificationChannel notificationChannel;
    private Boolean enabled;
    private String frequency;
    private String quietHoursStart;
    private String quietHoursEnd;
    private Boolean doNotDisturb;
}

