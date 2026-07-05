package com.aitasker.message.entity;
import com.aitasker.common.entity.BaseEntity;
import com.aitasker.project.entity.Project;
import com.aitasker.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Trước bản vá: projectId/senderId/receiverId là Long thô, không có ràng
 * buộc khóa ngoại ở tầng JPA/DB -> có thể insert Message trỏ tới project
 * hoặc user không tồn tại, và không join được trực tiếp trong query.
 * Sau bản vá: dùng @ManyToOne để Hibernate tạo FK thật + validate tồn tại.
 */
@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_message_project"))
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_message_sender"))
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_message_receiver"))
    private User receiver;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime sentAt;

    private Boolean isRead;
}
