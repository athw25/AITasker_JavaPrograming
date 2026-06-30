package com.aitasker.payment.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class DepositRequest {

    @NotNull(message = "projectId không được để trống")
    private Long projectId;

    private Long milestoneId; // optional

    @NotNull(message = "amount không được để trống")
    @DecimalMin(value = "0.01", message = "Số tiền phải lớn hơn 0")
    private BigDecimal amount;

    // Getters & Setters
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Long getMilestoneId() { return milestoneId; }
    public void setMilestoneId(Long milestoneId) { this.milestoneId = milestoneId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}