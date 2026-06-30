package com.aitasker.expert.entity;

import com.aitasker.common.entity.BaseEntity;

/**
 * Thực thể lưu trữ các dự án, sản phẩm nổi bật (Portfolio) của Chuyên gia.
 */
public class Portfolio extends BaseEntity {
    private Long id;
    private Long expertId;          // Khóa ngoại liên kết với ExpertProfile
    private String projectName;     // Tên dự án đã làm
    private String description;     // Mô tả chi tiết dự án
    private String projectUrl;      // Link sản phẩm demo (nếu có)

    public Portfolio() {}

    public Portfolio(Long id, Long expertId, String projectName, String description, String projectUrl) {
        this.id = id;
        this.expertId = expertId;
        this.projectName = projectName;
        this.description = description;
        this.projectUrl = projectUrl;
    }

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getExpertId() { return expertId; }
    public void setExpertId(Long expertId) { this.expertId = expertId; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getProjectUrl() { return projectUrl; }
    public void setProjectUrl(String projectUrl) { this.projectUrl = projectUrl; }
}