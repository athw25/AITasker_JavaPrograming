package com.aitasker.notification.service;

import com.aitasker.notification.entity.Notification;
import com.aitasker.notification.repository.NotificationRepository;
import com.aitasker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Notification createNotification(
            Long recipientId,
            String title,
            String content,
            String type
    ) {
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .title(title)
                .content(content)
                .type(type)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);

        messagingTemplate.convertAndSendToUser(
                String.valueOf(recipientId),
                "/queue/notifications",
                saved
        );

        return saved;
    }

    public List<Notification> getMyNotifications(User user) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId());
    }

    public Notification markAsRead(Long id, User user) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getRecipientId().equals(user.getId())) {
            throw new AccessDeniedException("You cannot read this notification");
        }

        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}
