package com.aitasker.milestone.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.common.enums.MilestoneStatus;
import com.aitasker.delivery.entity.Delivery;
import com.aitasker.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "milestones",
        indexes = {
                @Index(name = "idx_milestone_project", columnList = "project_id"),
                @Index(name = "idx_milestone_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Milestone extends BaseEntity {

    /**
     * Dự án chứa milestone.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_milestone_project")
    )
    private Project project;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "nvarchar(max)")
    private String description;

    /**
     * Giá trị milestone.
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /**
     * Hạn hoàn thành.
     */
    @Column(name = "due_date")
    private LocalDate dueDate;

    /**
     * Trạng thái milestone.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MilestoneStatus status;

    /**
     * Lần submit gần nhất.
     */
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    /**
     * Thời điểm được phê duyệt.
     */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /**
     * Danh sách delivery.
     */
    @OneToMany(
            mappedBy = "milestone",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    @OrderBy("version ASC")
    private List<Delivery> deliveries = new ArrayList<>();

    public void addDelivery(Delivery delivery) {
        deliveries.add(delivery);
        delivery.setMilestone(this);
    }

    public void removeDelivery(Delivery delivery) {
        deliveries.remove(delivery);
        delivery.setMilestone(null);
    }

}
