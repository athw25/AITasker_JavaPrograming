package com.aitasker.notification.controller;

import com.aitasker.notification.entity.Notification;
import com.aitasker.notification.service.NotificationService;
import com.aitasker.security.userdetails.CustomUserDetails;
import com.aitasker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(Authentication authentication) {
        User user = getCurrentUser(authentication);

        return ResponseEntity.ok(notificationService.getMyNotifications(user));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = getCurrentUser(authentication);

        return ResponseEntity.ok(notificationService.markAsRead(id, user));
    }

    private User getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser();
        }

        return (User) principal;
    }
}
