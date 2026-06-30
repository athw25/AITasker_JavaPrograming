package com.aitasker.expert.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePortfolioRequest {
    @NotBlank(message = "Tên dự án không được để trống")
    @Size(max = 100, message = "Tên dự án không quá 100 ký tự")
    private String projectName;

    @NotBlank(message = "Mô tả dự án không được để trống")
    private String description;

    private String projectUrl; // Không bắt buộc nhập

    // Getters và Setters
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getProjectUrl() { return projectUrl; }
    public void setProjectUrl(String projectUrl) { this.projectUrl = projectUrl; }
}