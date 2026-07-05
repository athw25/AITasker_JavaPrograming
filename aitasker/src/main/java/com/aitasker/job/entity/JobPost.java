package com.aitasker.job.entity;
import com.aitasker.common.entity.BaseEntity;
import com.aitasker.common.enums.JobStatus;
import com.aitasker.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "job_posts")
@Getter
@Setter
public class JobPost extends BaseEntity {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private BigDecimal budget;
    private LocalDate deadline;
    private String requiredSkills;
    @Enumerated(EnumType.STRING)
    private JobStatus status;

}
