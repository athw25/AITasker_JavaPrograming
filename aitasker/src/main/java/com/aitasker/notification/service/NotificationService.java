// NotificationService.java
package com.aitasker.notification.service;

import com.aitasker.notification.entity.Notification;
import com.aitasker.notification.repository.NotificationRepository;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void sendNotification(Long userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại để gửi thông báo"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);

        // Lưu thẳng xuống SQL Server
        notificationRepository.save(notification);
        System.out.println("✅ Đã lưu thông báo vào Database cho User ID: " + userId);
    }
}