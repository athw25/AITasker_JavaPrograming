package com.aitasker.project.dto.response;

import com.aitasker.common.enums.ProjectStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {

    private Long id;

    /**
     * Thông tin Client.
     */
    private Long clientId;
    private String clientName;

    /**
     * Thông tin Expert.
     */
    private Long expertId;
    private String expertName;

    /**
     * Job và Proposal.
     */
    private Long jobId;
    private String jobTitle;

    private Long proposalId;

    /**
     * Trạng thái Project.
     */
    private ProjectStatus status;

    /**
     * Ngày bắt đầu và kết thúc.
     */
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Tổng số milestone.
     */
    private Integer totalMilestones;

    /**
     * Số milestone hoàn thành.
     */
    private Integer completedMilestones;

    /**
     * Audit information.
     */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}