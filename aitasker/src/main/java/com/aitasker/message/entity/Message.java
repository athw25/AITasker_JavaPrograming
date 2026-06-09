package com.aitasker.message.entity;
import com.aitasker.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
public class Message extends BaseEntity {
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime sentAt;
    private Boolean isRead;
}
