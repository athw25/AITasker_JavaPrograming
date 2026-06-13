package com.aitasker.payment.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class WithdrawalRequest {

    @NotNull(message = "amount không được để trống")
    @DecimalMin(value = "0.01", message = "Số tiền rút phải lớn hơn 0")
    private BigDecimal amount;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}