package com.aitasker.review.repository;

import com.aitasker.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRevieweeId(Long revieweeId);
    boolean existsByReviewerIdAndProjectId(Long reviewerId, Long projectId);

    // Tính điểm đánh giá trung bình theo từng chuyên gia
    @Query("SELECT r.reviewee.id, AVG(CAST(r.rating AS double)) FROM Review r WHERE r.type = com.aitasker.common.enums.ReviewType.CLIENT_TO_EXPERT GROUP BY r.reviewee.id")
    List<Object[]> getAverageRatingsForExperts();
}
