package com.aitasker.review.repository;

import com.aitasker.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRevieweeId(Long revieweeId);
    boolean existsByReviewerIdAndProjectId(Long reviewerId, Long projectId);
}
