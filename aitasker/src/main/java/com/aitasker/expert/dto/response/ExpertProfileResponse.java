package com.aitasker.expert.dto.response;

public class ExpertProfileResponse {
    private Long id;
    private String fullName;
    private String title;
    private String skills;
    private int experienceYears;
    private double hourlyRate;

    // Nhận xét của TL: Tuyệt đối không đem trường internalNotes vào đây!

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
}