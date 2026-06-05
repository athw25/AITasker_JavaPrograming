package com.aitasker.user.entity;
import com.aitasker.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ExpertProfiles")
@Getter
@Setter
public class ExpertProfile extends BaseEntity {
    private String bio;
    private Integer experience;
    private String portfolio;
    private Double hourlyRate;
    private Double rating;
}
