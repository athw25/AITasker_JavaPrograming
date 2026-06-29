package com.aitasker.message.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChatMessageRequest {
    private Long projectId;
    private Long receiverId;
    private String content;
}
