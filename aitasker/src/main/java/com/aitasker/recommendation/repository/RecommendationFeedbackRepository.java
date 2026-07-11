package com.aitasker.recommendation.repository;

import com.aitasker.recommendation.entity.RecommendationFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationFeedbackRepository extends JpaRepository<RecommendationFeedback, Long> {
    Optional<RecommendationFeedback> findByJobIdAndExpertId(Long jobId, Long expertId);
    List<RecommendationFeedback> findByJobId(Long jobId);
}
