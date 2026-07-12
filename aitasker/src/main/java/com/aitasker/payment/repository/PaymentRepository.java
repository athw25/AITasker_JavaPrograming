package com.aitasker.payment.repository;

import com.aitasker.payment.entity.Payment;
import com.aitasker.payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Tìm payment theo project
    List<Payment> findByProjectId(Long projectId);

    // Tìm payment theo milestone (để kiểm tra trước khi release)
    Optional<Payment> findByMilestoneId(Long milestoneId);

    // Tìm payment theo project + status (kiểm tra HELD)
    List<Payment> findByProjectIdAndStatus(Long projectId, PaymentStatus status);

    long countByStatus(PaymentStatus status);

    @Query("""
            SELECT COALESCE(SUM(p.amount),0)
            FROM Payment p
            WHERE p.status = com.aitasker.payment.enums.PaymentStatus.RELEASED
            """)
    BigDecimal getTotalRevenue();

    @Query("""
            SELECT COALESCE(SUM(p.amount),0)
            FROM Payment p
            WHERE p.status = com.aitasker.payment.enums.PaymentStatus.RELEASED
            AND p.project.expert.id = :expertId
            """)
    BigDecimal getTotalReleasedForExpert(Long expertId);
}