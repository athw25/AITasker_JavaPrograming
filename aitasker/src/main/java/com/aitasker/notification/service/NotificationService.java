package com.aitasker.notification.service;

import com.aitasker.email.service.EmailService;
import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.notification.entity.Notification;
import com.aitasker.notification.repository.NotificationRepository;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;

    @Transactional
    public Notification createNotification(
            Long recipientId,
            String title,
            String content,
            String type
    ) {

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Recipient not found"));

        emailService.send(recipient.getEmail(), title, content);

        Notification notification = Notification.builder()
                .recipient(recipient)
                .title(title)
                .content(content)
                .type(type)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);

        /*
         * Gửi realtime
         * Nên dùng Principal.getName()
         * Nếu username=email thì thay bằng:
         * recipient.getEmail()
         */
        messagingTemplate.convertAndSendToUser(
                recipient.getEmail(),          // hoặc Principal Name
                "/queue/notifications",
                saved
        );

        return saved;
    }

    public List<Notification> getMyNotifications(User user) {

        return notificationRepository
                .findByRecipientOrderByCreatedAtDesc(user);
    }

    @Transactional
    public Notification markAsRead(Long id, User user) {

        Notification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Notification not found"));

        if (!notification.getRecipient().getId().equals(user.getId())) {

            throw new AccessDeniedException(
                    "You cannot access this notification");
        }

        notification.setRead(true);

        return notificationRepository.save(notification);
    }

}