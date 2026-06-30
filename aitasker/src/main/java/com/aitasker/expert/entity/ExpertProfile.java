package com.aitasker.expert.entity;
import com.aitasker.common.entity.BaseEntity;

/**
 * Hồ sơ gốc của Chuyên gia AI lưu dưới Database (chứa ghi chú nội bộ).
 */
public class ExpertProfile extends BaseEntity {
    private Long id;
    private String fullName;
    private String title;             // Ví dụ: Senior AI Engineer
    private String skills;            // Ví dụ: "Python, PyTorch, LLM"
    private int experienceYears;
    private double hourlyRate;        
    private String internalNotes;     // Thông tin ẩn bí mật của hệ thống

    public ExpertProfile() {}

    public ExpertProfile(Long id, String fullName, String title, String skills, int experienceYears, double hourlyRate, String internalNotes) {
        this.id = id;
        this.fullName = fullName;
        this.title = title;
        this.skills = skills;
        this.experienceYears = experienceYears;
        this.hourlyRate = hourlyRate;
        this.internalNotes = internalNotes;
    }

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }
    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }
}