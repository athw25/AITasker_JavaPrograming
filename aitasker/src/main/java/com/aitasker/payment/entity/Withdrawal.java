// src/main/java/com/aitasker/payment/entity/Withdrawal.java
package com.aitasker.payment.entity;

import com.aitasker.user.entity.User;
import com.aitasker.payment.enums.WithdrawalStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "withdrawals")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Withdrawal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Expert là người rút tiền
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id", nullable = false)
    private User expert;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WithdrawalStatus status; // PENDING, APPROVED, REJECTED

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime processedAt; // null cho đến khi Admin duyệt

    @PrePersist
    public void prePersist() {
        requestedAt = LocalDateTime.now();
        status = WithdrawalStatus.PENDING;
    }
}