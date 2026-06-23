package com.aitasker.delivery.dto.response;

import com.aitasker.common.enums.DeliveryStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponse {

    private Long id;

    /**
     * Milestone Information
     */
    private Long milestoneId;

    private String milestoneTitle;

    /**
     * Delivery Information
     */
    private String fileUrl;

    private String note;

    private Integer version;

    private DeliveryStatus status;

    /**
     * Submit Information
     */
    private Long submittedById;

    private String submittedByName;

    private LocalDateTime submittedAt;

    /**
     * Review Information
     */
    private LocalDateTime approvedAt;

    private String rejectReason;

    /**
     * Audit Information
     */
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}