package com.aitasker.proposal.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.common.enums.ProposalStatus;
import com.aitasker.job.entity.JobPost;
import com.aitasker.project.entity.Project;
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

    /**
     * Job mà Expert đang ứng tuyển.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "job_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_proposal_job")
    )
    private JobPost job;

    /**
     * Expert gửi Proposal.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "expert_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_proposal_expert")
    )
    private User expert;

    /**
     * Giá Expert đề xuất.
     */
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal bidAmount;

    /**
     * Thời gian hoàn thành dự kiến (ngày).
     */
    @Column(nullable = false)
    private Integer duration;

    /**
     * Thư giới thiệu.
     */
    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
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
    @Column(
            nullable = false,
            length = 20
    )
    private ProposalStatus status;

    /**
     * Project được tạo sau khi Proposal được chấp nhận.
     * Một Proposal chỉ sinh ra tối đa một Project.
     */
    @OneToOne(
            mappedBy = "proposal",
            fetch = FetchType.LAZY
    )
    private Project project;

    /**
     * Tự động gán giá trị mặc định khi persist.
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

    /**
     * Kiểm tra Proposal đã được chấp nhận.
     */
    public boolean isAccepted() {
        return ProposalStatus.ACCEPTED.equals(status);
    }

    /**
     * Kiểm tra Proposal còn chờ duyệt.
     */
    public boolean isPending() {
        return ProposalStatus.PENDING.equals(status);
    }

    /**
     * Kiểm tra Proposal đã bị từ chối.
     */
    public boolean isRejected() {
        return ProposalStatus.REJECTED.equals(status);
    }

    /**
     * Kiểm tra Proposal đã bị rút.
     */
    public boolean isWithdrawn() {
        return ProposalStatus.WITHDRAWN.equals(status);
    }

    /**
     * Có thể chỉnh sửa Proposal khi còn Pending.
     */
    public boolean canEdit() {
        return isPending();
    }

    /**
     * Có thể rút Proposal khi còn Pending.
     */
    public boolean canWithdraw() {
        return isPending();
    }

}