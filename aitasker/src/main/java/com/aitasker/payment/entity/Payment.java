package com.aitasker.payment.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.milestone.entity.Milestone;
import com.aitasker.payment.enums.PaymentStatus;
import com.aitasker.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Kế thừa BaseEntity thay vì tự khai báo lại id/createdAt/updatedAt/@PrePersist/@PreUpdate
// (trước đây bị lặp code y hệt BaseEntity, không đồng nhất với các entity khác trong hệ thống).
public class Payment extends BaseEntity {

    // Quan hệ với Project (bắt buộc)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // Quan hệ với Milestone (optional — thanh toán theo milestone)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id")
    private Milestone milestone;

    // Số tiền dùng BigDecimal cho chính xác tài chính
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    // Trạng thái: PENDING → HELD → RELEASED / REFUNDED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // Mã giao dịch tham chiếu (để tra cứu)
    @Column(name = "transaction_ref")
    private String transactionRef;
}