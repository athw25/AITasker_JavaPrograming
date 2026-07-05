package com.aitasker.notification.repository;

import com.aitasker.notification.entity.NotificationPreference;
import com.aitasker.notification.enums.NotificationChannel;
import com.aitasker.notification.enums.NotificationType;
import com.aitasker.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {
    List<NotificationPreference> findByUser(User user);

    List<NotificationPreference> findByUserAndEnabledTrue(User user);

    List<NotificationPreference> findByUserAndNotificationType(User user, NotificationType notificationType);

    List<NotificationPreference> findByUserAndNotificationChannel(User user, NotificationChannel notificationChannel);

    Optional<NotificationPreference> findByUserAndNotificationTypeAndNotificationChannel(
            User user, NotificationType notificationType, NotificationChannel notificationChannel);

    List<NotificationPreference> findByUserAndNotificationTypeAndEnabledTrue(User user, NotificationType notificationType);

    List<NotificationPreference> findByNotificationChannelAndEnabledTrue(NotificationChannel notificationChannel);

    void deleteByUserAndNotificationTypeAndNotificationChannel(User user, NotificationType notificationType, NotificationChannel notificationChannel);
}

