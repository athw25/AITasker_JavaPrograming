package com.aitasker.expert.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class UpdateExpertProfileRequest {
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 50, message = "Tên phải từ 2 đến 50 ký tự")
    private String fullName;

    @NotBlank(message = "Vị trí chuyên môn không được để trống")
    private String title;

    @NotBlank(message = "Kỹ năng không được để trống")
    private String skills;

    @NotNull(message = "Số năm kinh nghiệm không được để trống")
    @PositiveOrZero(message = "Số năm kinh nghiệm không được là số âm")
    private Integer experienceYears;

    @NotNull(message = "Mức giá theo giờ không được để trống")
    @PositiveOrZero(message = "Mức giá không được là số âm")
    private BigDecimal hourlyRate;

    // Getters và Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }
}