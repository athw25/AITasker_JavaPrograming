package com.aitasker.milestone.dto.response;

import com.aitasker.common.enums.MilestoneStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilestoneResponse {

    private Long id;

    /**
     * Project Information
     */
    private Long projectId;

    /**
     * Milestone Information
     */
    private String title;

    private String description;

    private BigDecimal amount;

    private LocalDate dueDate;

    private MilestoneStatus status;

    /**
     * Delivery Statistics
     */
    private Integer totalDeliveries;

    private Integer latestVersion;

    /**
     * Submit Information
     */
    private LocalDateTime submittedAt;

    /**
     * Approve Information
     */
    private LocalDateTime approvedAt;

    /**
     * Payment Information
     */
    private Boolean paid;

    /**
     * Audit Information
     */
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}