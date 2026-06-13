package com.aitasker.expert.dto;

public class ServicePackageDto {
    private Long id;
    private Long expertId;
    private String packageName;
    private double price;
    private int deliveryDays;

    public ServicePackageDto() {}

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getExpertId() { return expertId; }
    public void setExpertId(Long expertId) { this.expertId = expertId; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getDeliveryDays() { return deliveryDays; }
    public void setDeliveryDays(int deliveryDays) { this.deliveryDays = deliveryDays; }
}