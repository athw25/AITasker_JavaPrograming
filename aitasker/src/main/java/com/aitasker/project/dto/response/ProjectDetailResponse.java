package com.aitasker.project.dto.response;

import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.milestone.dto.response.MilestoneResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDetailResponse {

    private Long id;

    /**
     * Client Information
     */
    private Long clientId;
    private String clientName;
    private String clientEmail;

    /**
     * Expert Information
     */
    private Long expertId;
    private String expertName;
    private String expertEmail;

    /**
     * Job Information
     */
    private Long jobId;
    private String jobTitle;

    /**
     * Proposal Information
     */
    private Long proposalId;

    /**
     * Project Information
     */
    private ProjectStatus status;

    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Statistics
     */
    private Integer totalMilestones;
    private Integer completedMilestones;

    private BigDecimal totalBudget;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;

    /**
     * Milestone List
     */
    private List<MilestoneResponse> milestones;

    /**
     * Audit Information
     */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}