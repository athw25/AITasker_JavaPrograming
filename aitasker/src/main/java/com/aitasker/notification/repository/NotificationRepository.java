package com.aitasker.notification.repository;

import com.aitasker.notification.entity.Notification;
import com.aitasker.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Lấy danh sách notification của một User,
     * mới nhất trước.
     */
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);

    /**
     * Hoặc nếu Service chỉ có userId.
     */
    List<Notification> findByRecipient_IdOrderByCreatedAtDesc(Long recipientId);
}