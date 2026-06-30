package com.aitasker.proposal.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.common.enums.ProposalStatus;
import com.aitasker.job.entity.JobPost;
import com.aitasker.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "proposals",
        indexes = {
                @Index(
                        name = "idx_proposal_job",
                        columnList = "job_id"
                ),
                @Index(
                        name = "idx_proposal_expert",
                        columnList = "expert_id"
                ),
                @Index(
                        name = "idx_proposal_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proposal extends BaseEntity {

    /**
     * Job mà Expert đang ứng tuyển.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "job_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_proposal_job"
            )
    )
    private JobPost job;

    /**
     * Expert gửi Proposal.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "expert_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_proposal_expert"
            )
    )
    private User expert;

    /**
     * Giá đề xuất của Expert.
     */
    @Column(nullable = false)
    private Double bidAmount;

    /**
     * Thời gian thực hiện dự án (ngày).
     */
    @Column(nullable = false)
    private Integer duration;

    /**
     * Thư giới thiệu.
     */
    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    /**
     * Thời điểm gửi Proposal.
     */
    @Column(nullable = false)
    private LocalDateTime submittedAt;

    /**
     * Trạng thái Proposal.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProposalStatus status;

    /**
     * Tự động set dữ liệu khi persist.
     */
    @PrePersist
    public void prePersist() {

        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }

        if (status == null) {
            status = ProposalStatus.PENDING;
        }
    }
}