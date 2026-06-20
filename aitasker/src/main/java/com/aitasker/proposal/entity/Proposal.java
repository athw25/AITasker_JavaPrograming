// Proposal.java
package com.aitasker.proposal.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.common.enums.ProposalStatus;
import com.aitasker.job.entity.JobPost;
import com.aitasker.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "proposals")
@Getter
@Setter
public class Proposal extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobPost job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id", nullable = false)
    private User expert;

    @Column(nullable = false)
    private Double bidAmount;

    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProposalStatus status;
}