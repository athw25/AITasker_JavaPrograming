package com.aitasker.review.repository;

import com.aitasker.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRevieweeId(Long revieweeId);
    boolean existsByReviewerIdAndProjectId(Long reviewerId, Long projectId);
    @Query("""
            SELECT COALESCE(AVG(r.rating), 0)
            FROM Review r
            """)
    Double getAverageRating();

}
