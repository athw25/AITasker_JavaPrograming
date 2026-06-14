package com.aitasker.project.entity;
import com.aitasker.common.entity.BaseEntity;
import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
@Entity
@Table(name = "projects")
@Getter
@Setter
public class Project extends BaseEntity {
    private String projectName;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer totalBudget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id")
    private User expert;
}
