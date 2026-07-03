package com.aitasker.expert.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UpdateServicePackageRequest {
    @NotBlank(message = "Tên gói dịch vụ không được để trống")
    private String packageName;

    @NotNull(message = "Giá gói dịch vụ không được để trống")
    @Positive(message = "Giá dịch vụ phải lớn hơn 0")
    private Double price;

    @NotNull(message = "Số ngày bàn giao không được để trống")
    @Positive(message = "Số ngày bàn giao phải lớn hơn 0")
    private Integer deliveryDays;

    // Getters và Setters
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getDeliveryDays() { return deliveryDays; }
    public void setDeliveryDays(Integer deliveryDays) { this.deliveryDays = deliveryDays; }
}