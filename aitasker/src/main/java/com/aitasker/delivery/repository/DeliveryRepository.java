package com.aitasker.delivery.repository;

import com.aitasker.delivery.entity.Delivery;
import com.aitasker.milestone.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository
        extends JpaRepository<Delivery, Long> {

    /**
     * Lấy tất cả delivery của milestone.
     */
    List<Delivery> findByMilestoneIdOrderByVersionAsc(
            Long milestoneId
    );

    /**
     * Lấy delivery theo milestone.
     */
    List<Delivery> findByMilestone(
            Milestone milestone
    );

    /**
     * Lấy delivery mới nhất.
     */
    Optional<Delivery>
    findTopByMilestoneIdOrderByVersionDesc(
            Long milestoneId
    );

    /**
     * Đếm số lần submit.
     */
    long countByMilestoneId(
            Long milestoneId
    );

    /**
     * Kiểm tra delivery thuộc milestone.
     */
    boolean existsByIdAndMilestoneId(
            Long deliveryId,
            Long milestoneId
    );

    /**
     * Lấy delivery theo version.
     */
    Optional<Delivery>
    findByMilestoneIdAndVersion(
            Long milestoneId,
            Integer version
    );

    /**
     * Lấy toàn bộ delivery theo thứ tự version.
     */
    /**
     * Lấy delivery mới nhất.
     */
    List<Delivery>
    findByMilestoneIdOrderByVersionDesc(
            Long milestoneId
    );
}
