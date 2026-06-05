package com.aitasker.payment.entity;
import com.aitasker.common.entity.BaseEntity;
import com.aitasker.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment extends BaseEntity {
    private Double amount;
    private LocalDate paymentDate;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}
