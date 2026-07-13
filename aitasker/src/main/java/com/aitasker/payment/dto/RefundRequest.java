package com.aitasker.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RefundRequest {
    @NotNull(message = "amount không được để trống")
    @DecimalMin(value = "0.01", message = "amount phải lớn hơn 0")
    private BigDecimal amount;

    @NotBlank(message = "reason không được để trống")
    private String reason;
}
