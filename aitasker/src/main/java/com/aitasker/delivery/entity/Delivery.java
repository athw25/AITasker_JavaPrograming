package com.aitasker.delivery.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.common.enums.DeliveryStatus;
import com.aitasker.milestone.entity.Milestone;
import com.aitasker.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "deliveries",
        indexes = {
                @Index(name = "idx_delivery_milestone", columnList = "milestone_id"),
                @Index(name = "idx_delivery_submitter", columnList = "submitted_by")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Milestone chứa delivery.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "milestone_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_delivery_milestone")
    )
    private Milestone milestone;

    /**
     * Đường dẫn file.
     */
    @Column(nullable = false, length = 1000)
    private String fileUrl;

    /**
     * Ghi chú khi nộp.
     */
    @Column(columnDefinition = "nvarchar(max)")
    private String note;

    /**
     * Phiên bản bàn giao.
     */
    @Column(nullable = false)
    private Integer version;

    /**
     * Người nộp.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "submitted_by",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_delivery_submitter")
    )
    private User submittedBy;

    /**
     * Thời gian nộp.
     */
    @Column(nullable = false)
    private LocalDateTime submittedAt;
    
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    private LocalDateTime approvedAt;

    private String rejectReason;



}