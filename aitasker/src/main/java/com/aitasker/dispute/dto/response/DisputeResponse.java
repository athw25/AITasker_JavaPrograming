package com.aitasker.dispute.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.aitasker.common.enums.DisputeStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DisputeResponse {

    private Long id;
    private Long projectId;
    private String projectTitle;

    private Long createdById;
    private String createdByName;

    private String reason;
    private DisputeStatus status;
    private String resolution;
    private LocalDateTime resolvedAt;

    private List<EvidenceItem> evidences;
    private List<MessageItem> messages;

    private LocalDateTime createdAt;

    @Getter
    @Setter
    @Builder
    public static class EvidenceItem {
        private Long id;
        private String fileUrl;
        private String description;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @Builder
    public static class MessageItem {
        private Long id;
        private Long senderId;
        private String senderName;
        private String message;
        private LocalDateTime createdAt;
    }
}