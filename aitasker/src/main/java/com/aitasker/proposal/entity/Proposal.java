package com.aitasker.proposal.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.common.enums.ProposalStatus;
import com.aitasker.job.entity.JobPost;
import com.aitasker.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
    private BigDecimal bidAmount;
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