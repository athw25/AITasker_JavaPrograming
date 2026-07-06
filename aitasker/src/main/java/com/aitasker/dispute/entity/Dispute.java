package com.aitasker.dispute.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.common.enums.DisputeStatus;
import com.aitasker.project.entity.Project;
import com.aitasker.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "disputes",
        indexes = {
                @Index(name = "idx_dispute_project", columnList = "project_id"),
                @Index(name = "idx_dispute_status", columnList = "status"),
                @Index(name = "idx_dispute_created_by", columnList = "created_by")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dispute extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_dispute_project")
    )
    private Project project;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "created_by",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_dispute_created_by")
    )
    private User createdBy;
    @Column(nullable = false, columnDefinition = "nvarchar(max)")
    private String reason;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DisputeStatus status;
    @Column(columnDefinition = "nvarchar(max)")
    private String resolution;
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @OneToMany(
            mappedBy = "dispute",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    @OrderBy("createdAt ASC")
    private List<DisputeMessage> messages = new ArrayList<>();

    @OneToMany(
            mappedBy = "dispute",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    @OrderBy("createdAt ASC")
    private List<DisputeEvidence> evidences = new ArrayList<>();
    public void addMessage(DisputeMessage message) {
        messages.add(message);
        message.setDispute(this);
    }
    public void addEvidence(DisputeEvidence evidence) {
        evidences.add(evidence);
        evidence.setDispute(this);
    }
}