package com.aitasker.project.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.job.entity.JobPost;
import com.aitasker.proposal.entity.Proposal;
import com.aitasker.user.entity.User;
import com.aitasker.milestone.entity.Milestone;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "projects",
        indexes = {
                @Index(name = "idx_project_client", columnList = "client_id"),
                @Index(name = "idx_project_expert", columnList = "expert_id"),
                @Index(name = "idx_project_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project extends BaseEntity {

    /**
     * Người thuê dự án.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "client_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_project_client")
    )
    private User client;

    /**
     * Chuyên gia thực hiện dự án.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "expert_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_project_expert")
    )
    private User expert;

    /**
     * Job được chuyển thành Project.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "job_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_project_job")
    )
    private JobPost job;

    /**
     * Proposal được chấp nhận.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "proposal_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_project_proposal")
    )
    private Proposal proposal;

    /**
     * Ngày bắt đầu dự án.
     */
    @Column(name = "start_date")
    private LocalDate startDate;

    /**
     * Ngày kết thúc dự kiến.
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Trạng thái dự án.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectStatus status;

    /**
     * Danh sách Milestone.
     */
    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    @OrderBy("createdAt ASC")
    private List<Milestone> milestones = new ArrayList<>();

    public void addMilestone(Milestone milestone) {
        milestones.add(milestone);
        milestone.setProject(this);
    }

    public void removeMilestone(Milestone milestone) {
        milestones.remove(milestone);
        milestone.setProject(null);
    }
}
