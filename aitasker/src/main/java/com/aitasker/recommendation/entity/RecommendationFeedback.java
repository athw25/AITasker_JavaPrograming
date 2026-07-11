package com.aitasker.recommendation.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.job.entity.JobPost;
import com.aitasker.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recommendation_feedbacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationFeedback extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobPost job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id", nullable = false)
    private User expert;

    @Column(nullable = false)
    private boolean recommended;

    @Column(nullable = false)
    private boolean hired;
}
