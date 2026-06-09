package com.aitasker.milestone.entity;
import com.aitasker.common.entity.BaseEntity;
import com.aitasker.common.enums.MilestoneStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "milestone")
@Getter
@Setter
public class Milestone extends BaseEntity {
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private MilestoneStatus status;
}
