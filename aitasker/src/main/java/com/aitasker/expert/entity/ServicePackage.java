package com.aitasker.expert.entity;

import com.aitasker.common.entity.BaseEntity;

/**
 * Gói dịch vụ AI chuyên gia đăng bán trên chợ (kế thừa BaseEntity để tự động lưu ngày tạo).
 */
public class ServicePackage extends BaseEntity {
    private Long id;
    private Long expertId;         // Id của chuyên gia sở hữu gói dịch vụ này
    private String packageName;     // Ví dụ: Xây dựng Chatbot AI tự động
    private double price;
    private int deliveryDays;      // Số ngày hoàn thành bàn giao dự kiến

    public ServicePackage() {}

    public ServicePackage(Long id, Long expertId, String packageName, double price, int deliveryDays) {
        this.id = id;
        this.expertId = expertId;
        this.packageName = packageName;
        this.price = price;
        this.deliveryDays = deliveryDays;
    }

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
