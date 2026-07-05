package com.aitasker.payment.dto;

import com.aitasker.payment.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {
    private Long id;
    private Long projectId;
    private Long milestoneId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String transactionRef;
    private LocalDateTime createdAt;

    // Getters & Setters (hoặc dùng @Getter @Setter của Lombok)
}