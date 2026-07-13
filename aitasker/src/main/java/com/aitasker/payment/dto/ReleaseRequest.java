package com.aitasker.payment.dto;

import jakarta.validation.constraints.NotNull;

public class ReleaseRequest {

    @NotNull(message = "paymentId không được để trống")
    private Long paymentId;

    public ReleaseRequest() {}

    public ReleaseRequest(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
}