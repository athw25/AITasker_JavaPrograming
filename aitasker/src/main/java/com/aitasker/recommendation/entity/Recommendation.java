package com.aitasker.recommendation.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.job.entity.JobPost;
import com.aitasker.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recommendations")
@Getter
@Setter
@NoArgsConstructor
public class Recommendation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobPost job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id", nullable = false)
    private User expert;

    @Column(nullable = false)
    private Double matchScore;

    @Column(name = "skill_score", nullable = false)
    private Double skillScore;

    @Column(name = "rating_score", nullable = false)
    private Double ratingScore;

    @Column(name = "success_rate_score", nullable = false)
    private Double successRateScore;

    @Column(name = "experience_score", nullable = false)
    private Double experienceScore;

    @Column(name = "portfolio_score", nullable = false)
    private Double portfolioScore; // Bổ sung để đáp ứng yêu cầu số 1

    @Column(nullable = false)
    private boolean isAccepted = false;
}
