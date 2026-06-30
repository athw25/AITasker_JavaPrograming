package com.aitasker.expert.dto.response;

public class PortfolioResponse {
    private Long id;
    private Long expertId;
    private String projectName;
    private String description;
    private String projectUrl;

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