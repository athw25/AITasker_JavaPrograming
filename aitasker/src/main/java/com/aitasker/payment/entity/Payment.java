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
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id")
    private Milestone milestone;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    // Số tiền còn giữ trong escrow (giảm dần khi refund một phần).
    // Khởi tạo bằng amount lúc deposit; = 0 khi đã refund/release hết.
    @Column(name = "remaining_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal remainingAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "transaction_ref")
    private String transactionRef;

    // Optimistic locking — chống 2 request deposit/release/refund cùng lúc
    // ghi đè trạng thái của nhau (race condition).
    @Version
    private Long version;
}