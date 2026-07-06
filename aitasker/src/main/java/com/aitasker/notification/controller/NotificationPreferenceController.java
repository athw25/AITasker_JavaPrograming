package com.aitasker.notification.controller;

import com.aitasker.notification.dto.NotificationPreferenceDTO;
import com.aitasker.notification.entity.NotificationPreference;
import com.aitasker.notification.enums.NotificationChannel;
import com.aitasker.notification.enums.NotificationType;
import com.aitasker.notification.service.NotificationPreferenceService;
import com.aitasker.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notification-preferences")
@RequiredArgsConstructor
@Tag(name = "Notification Preferences", description = "User notification preference management")
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create or update notification preference")
    public ResponseEntity<ApiResponse> createOrUpdatePreference(
            @RequestParam Long userId,
            @RequestParam NotificationType notificationType,
            @RequestParam NotificationChannel notificationChannel,
            @RequestParam(defaultValue = "true") Boolean enabled,
            @RequestParam(defaultValue = "IMMEDIATE") String frequency,
            @RequestParam(required = false) String quietHoursStart,
            @RequestParam(required = false) String quietHoursEnd) {
        try {
            NotificationPreference pref = preferenceService.createOrUpdatePreference(
                    userId, notificationType, notificationChannel, enabled, frequency, quietHoursStart, quietHoursEnd);
            return ResponseEntity.ok(ApiResponse.success(convertToDTO(pref)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all preferences for user")
    public ResponseEntity<ApiResponse> getUserPreferences(@PathVariable Long userId) {
        try {
            List<NotificationPreference> prefs = preferenceService.getUserPreferences(userId);
            List<NotificationPreferenceDTO> dtos = prefs.stream().map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(dtos));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/enabled")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get enabled preferences for user")
    public ResponseEntity<ApiResponse> getEnabledUserPreferences(@PathVariable Long userId) {
        try {
            List<NotificationPreference> prefs = preferenceService.getEnabledUserPreferences(userId);
            List<NotificationPreferenceDTO> dtos = prefs.stream().map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(dtos));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/type/{notificationType}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get preferences by notification type")
    public ResponseEntity<ApiResponse> getPreferencesByType(
            @PathVariable Long userId,
            @PathVariable NotificationType notificationType) {
        try {
            List<NotificationPreference> prefs = preferenceService.getPreferencesByType(userId, notificationType);
            List<NotificationPreferenceDTO> dtos = prefs.stream().map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(dtos));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/channel/{notificationChannel}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get preferences by notification channel")
    public ResponseEntity<ApiResponse> getPreferencesByChannel(
            @PathVariable Long userId,
            @PathVariable NotificationChannel notificationChannel) {
        try {
            List<NotificationPreference> prefs = preferenceService.getPreferencesByChannel(userId, notificationChannel);
            List<NotificationPreferenceDTO> dtos = prefs.stream().map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(dtos));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/type/{notificationType}/channel/{notificationChannel}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get specific preference")
    public ResponseEntity<ApiResponse> getPreference(
            @PathVariable Long userId,
            @PathVariable NotificationType notificationType,
            @PathVariable NotificationChannel notificationChannel) {
        try {
            Optional<NotificationPreference> pref = preferenceService.getPreference(userId, notificationType, notificationChannel);
            if (pref.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(convertToDTO(pref.get())));
            }
            return ResponseEntity.ok(ApiResponse.fail("Preference not found"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @DeleteMapping("/user/{userId}/type/{notificationType}/channel/{notificationChannel}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete preference")
    public ResponseEntity<ApiResponse> deletePreference(
            @PathVariable Long userId,
            @PathVariable NotificationType notificationType,
            @PathVariable NotificationChannel notificationChannel) {
        try {
            preferenceService.deletePreference(userId, notificationType, notificationChannel);
            return ResponseEntity.ok(ApiResponse.success("Preference deleted"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @PostMapping("/user/{userId}/disable-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Disable all notifications for user")
    public ResponseEntity<ApiResponse> disableAllNotifications(@PathVariable Long userId) {
        try {
            preferenceService.disableAllNotifications(userId);
            return ResponseEntity.ok(ApiResponse.success("All notifications disabled"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @PostMapping("/user/{userId}/enable-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Enable all notifications for user")
    public ResponseEntity<ApiResponse> enableAllNotifications(@PathVariable Long userId) {
        try {
            preferenceService.enableAllNotifications(userId);
            return ResponseEntity.ok(ApiResponse.success("All notifications enabled"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @PostMapping("/user/{userId}/initialize-defaults")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Initialize default preferences for new user")
    public ResponseEntity<ApiResponse> initializeDefaultPreferences(@PathVariable Long userId) {
        try {
            preferenceService.initializeDefaultPreferences(userId);
            return ResponseEntity.ok(ApiResponse.success("Default preferences initialized"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/check/{notificationType}/{notificationChannel}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Check if notification is enabled")
    public ResponseEntity<ApiResponse> isNotificationEnabled(
            @PathVariable Long userId,
            @PathVariable NotificationType notificationType,
            @PathVariable NotificationChannel notificationChannel) {
        try {
            boolean enabled = preferenceService.isNotificationEnabled(userId, notificationType, notificationChannel);
            return ResponseEntity.ok(ApiResponse.success(enabled));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/quiet-hours/{notificationChannel}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Check if it's quiet hours")
    public ResponseEntity<ApiResponse> isQuietHours(
            @PathVariable Long userId,
            @PathVariable NotificationChannel notificationChannel) {
        try {
            boolean quietHours = preferenceService.isQuietHours(userId, notificationChannel);
            return ResponseEntity.ok(ApiResponse.success(quietHours));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    private NotificationPreferenceDTO convertToDTO(NotificationPreference pref) {
        return NotificationPreferenceDTO.builder()
                .id(pref.getId())
                .userId(pref.getUser().getId())
                .notificationType(pref.getNotificationType())
                .notificationChannel(pref.getNotificationChannel())
                .enabled(pref.getEnabled())
                .frequency(pref.getFrequency())
                .quietHoursStart(pref.getQuietHoursStart())
                .quietHoursEnd(pref.getQuietHoursEnd())
                .doNotDisturb(pref.getDoNotDisturb())
                .build();
    }
}

