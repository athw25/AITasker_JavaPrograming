// src/main/java/com/aitasker/payment/entity/Transaction.java
package com.aitasker.payment.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.payment.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity {

    // Mỗi Transaction thuộc về một Payment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // DEPOSIT, RELEASE, REFUND, WITHDRAWAL

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    private String description;
}