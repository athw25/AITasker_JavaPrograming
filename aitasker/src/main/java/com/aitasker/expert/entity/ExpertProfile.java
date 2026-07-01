package com.aitasker.expert.entity;
import com.aitasker.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Hồ sơ gốc của Chuyên gia AI lưu dưới Database (chứa ghi chú nội bộ).
 */
@Entity
@Table(name = "expert_profiles")
@Getter
@Setter
@NoArgsConstructor
public class ExpertProfile extends BaseEntity {
    private String fullName;
    private String title;             // Ví dụ: Senior AI Engineer
    private String skills;            // Ví dụ: "Python, PyTorch, LLM"
    private int experienceYears;
    private double hourlyRate;
    @Column(columnDefinition = "TEXT")
    private String internalNotes;     // Thông tin ẩn bí mật của hệ thống
}