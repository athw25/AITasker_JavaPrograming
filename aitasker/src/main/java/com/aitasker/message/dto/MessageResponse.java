package com.aitasker.message.dto;

import com.aitasker.message.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO trả về cho client thay vì trả thẳng entity Message (tránh serialize
 * toàn bộ User sender/receiver — kể cả khi password đã @JsonIgnore, DTO vẫn
 * là cách kiểm soát tường minh dữ liệu nào được lộ ra ngoài).
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private Long projectId;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String content;
    private LocalDateTime sentAt;
    private Boolean isRead;

    public static MessageResponse from(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getProject() != null ? message.getProject().getId() : null,
                message.getSender() != null ? message.getSender().getId() : null,
                message.getSender() != null ? message.getSender().getName() : null,
                message.getReceiver() != null ? message.getReceiver().getId() : null,
                message.getContent(),
                message.getSentAt(),
                message.getIsRead()
        );
    }
}
