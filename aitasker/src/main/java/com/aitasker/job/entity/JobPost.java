package com.aitasker.job.entity;
import com.aitasker.common.entity.BaseEntity;
import com.aitasker.common.enums.JobStatus;
import com.aitasker.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "job_posts")
@Getter
@Setter
public class JobPost extends BaseEntity {
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Double budget;
    private LocalDate deadline;
    private String requiredSkills;
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Client_id")
    private User client;

}