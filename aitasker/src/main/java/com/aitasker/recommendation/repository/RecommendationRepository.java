package com.aitasker.recommendation.repository;

import com.aitasker.recommendation.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    Optional<Recommendation> findByJobIdAndExpertId(Long jobId, Long expertId);
    List<Recommendation> findByJobId(Long jobId);
}