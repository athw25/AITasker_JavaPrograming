package com.aitasker.recommendation.repository;

import com.aitasker.recommendation.entity.Recommendation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    @Query("SELECT r FROM Recommendation r JOIN FETCH r.job JOIN FETCH r.expert WHERE r.job.id = :jobId AND r.expert.id = :expertId")
    Optional<Recommendation> findByJobIdAndExpertId(@Param("jobId") Long jobId, @Param("expertId") Long expertId);

    @Query("SELECT r FROM Recommendation r JOIN FETCH r.job JOIN FETCH r.expert WHERE r.job.id = :jobId")
    List<Recommendation> findByJobId(@Param("jobId") Long jobId);

    @Query(value = "SELECT r FROM Recommendation r JOIN FETCH r.job JOIN FETCH r.expert",
            countQuery = "SELECT COUNT(r) FROM Recommendation r")
    Page<Recommendation> findAllWithJobAndExpert(Pageable pageable);

    @Query("SELECT COUNT(r) FROM Recommendation r")
    long countOverall();

    @Query("SELECT COUNT(r) FROM Recommendation r WHERE r.isAccepted = true")
    long countAcceptedOverall();

    @Query("SELECT AVG(r.matchScore) FROM Recommendation r")
    Double getAverageMatchScoreOverall();

    @Query("SELECT AVG(r.matchScore) FROM Recommendation r WHERE r.isAccepted = true")
    Double getAverageMatchScoreOfAccepted();
}
