package com.aitasker.notification.service;

import com.aitasker.notification.entity.NotificationPreference;
import com.aitasker.notification.repository.NotificationPreferenceRepository;
import com.aitasker.notification.enums.NotificationChannel;
import com.aitasker.notification.enums.NotificationType;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    @Transactional
    public NotificationPreference createOrUpdatePreference(Long userId, NotificationType notificationType,
                                                           NotificationChannel notificationChannel,
                                                           Boolean enabled, String frequency,
                                                           String quietHoursStart, String quietHoursEnd) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<NotificationPreference> existing = preferenceRepository
                .findByUserAndNotificationTypeAndNotificationChannel(user, notificationType, notificationChannel);

        NotificationPreference preference = existing.orElseGet(() ->
                NotificationPreference.builder()
                        .user(user)
                        .notificationType(notificationType)
                        .notificationChannel(notificationChannel)
                        .build()
        );

        preference.setEnabled(enabled);
        preference.setFrequency(frequency != null ? frequency : "IMMEDIATE");
        preference.setQuietHoursStart(quietHoursStart);
        preference.setQuietHoursEnd(quietHoursEnd);

        NotificationPreference saved = preferenceRepository.save(preference);
        log.info("Notification preference saved for user: {} - Type: {}, Channel: {}", userId, notificationType, notificationChannel);
        return saved;
    }

    public List<NotificationPreference> getUserPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return preferenceRepository.findByUser(user);
    }

    public List<NotificationPreference> getEnabledUserPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return preferenceRepository.findByUserAndEnabledTrue(user);
    }

    public List<NotificationPreference> getPreferencesByType(Long userId, NotificationType notificationType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return preferenceRepository.findByUserAndNotificationType(user, notificationType);
    }

    public List<NotificationPreference> getPreferencesByChannel(Long userId, NotificationChannel notificationChannel) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return preferenceRepository.findByUserAndNotificationChannel(user, notificationChannel);
    }

    public Optional<NotificationPreference> getPreference(Long userId, NotificationType notificationType,
                                                          NotificationChannel notificationChannel) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return preferenceRepository.findByUserAndNotificationTypeAndNotificationChannel(user, notificationType, notificationChannel);
    }

    @Transactional
    public void deletePreference(Long userId, NotificationType notificationType, NotificationChannel notificationChannel) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        preferenceRepository.deleteByUserAndNotificationTypeAndNotificationChannel(user, notificationType, notificationChannel);
        log.info("Notification preference deleted for user: {} - Type: {}, Channel: {}", userId, notificationType, notificationChannel);
    }

    public boolean isNotificationEnabled(Long userId, NotificationType notificationType, NotificationChannel notificationChannel) {
        Optional<NotificationPreference> pref = getPreference(userId, notificationType, notificationChannel);
        return pref.map(NotificationPreference::getEnabled).orElse(true);
    }

    public boolean isQuietHours(Long userId, NotificationChannel notificationChannel) {
        List<NotificationPreference> prefs = preferenceRepository.findByUserAndNotificationChannel(
                userRepository.findById(userId).orElseThrow(), notificationChannel);

        if (prefs.isEmpty()) {
            return false;
        }

        NotificationPreference pref = prefs.get(0);
        if (Boolean.TRUE.equals(pref.getDoNotDisturb())) {
            return true;
        }

        if (pref.getQuietHoursStart() != null && pref.getQuietHoursEnd() != null) {
            java.time.LocalTime now = java.time.LocalTime.now();
            java.time.LocalTime start = java.time.LocalTime.parse(pref.getQuietHoursStart());
            java.time.LocalTime end = java.time.LocalTime.parse(pref.getQuietHoursEnd());

            if (start.isBefore(end)) {
                return now.isAfter(start) && now.isBefore(end);
            } else {
                return now.isAfter(start) || now.isBefore(end);
            }
        }

        return false;
    }

    @Transactional
    public void disableAllNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<NotificationPreference> allPrefs = preferenceRepository.findByUser(user);
        allPrefs.forEach(pref -> pref.setEnabled(false));
        preferenceRepository.saveAll(allPrefs);
        log.info("All notifications disabled for user: {}", userId);
    }

    @Transactional
    public void enableAllNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<NotificationPreference> allPrefs = preferenceRepository.findByUser(user);
        allPrefs.forEach(pref -> pref.setEnabled(true));
        preferenceRepository.saveAll(allPrefs);
        log.info("All notifications enabled for user: {}", userId);
    }

    @Transactional
    public void initializeDefaultPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        for (NotificationType type : NotificationType.values()) {
            for (NotificationChannel channel : NotificationChannel.values()) {
                Optional<NotificationPreference> existing = preferenceRepository
                        .findByUserAndNotificationTypeAndNotificationChannel(user, type, channel);

                if (existing.isEmpty()) {
                    NotificationPreference pref = NotificationPreference.builder()
                            .user(user)
                            .notificationType(type)
                            .notificationChannel(channel)
                            .enabled(true)
                            .frequency("IMMEDIATE")
                            .doNotDisturb(false)
                            .build();
                    preferenceRepository.save(pref);
                }
            }
        }
        log.info("Default notification preferences initialized for user: {}", userId);
    }
}

