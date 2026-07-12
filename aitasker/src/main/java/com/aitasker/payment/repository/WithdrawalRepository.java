package com.aitasker.payment.repository;

import com.aitasker.payment.entity.Withdrawal;
import com.aitasker.payment.enums.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {

    // Expert xem withdrawal của mình
    List<Withdrawal> findByExpertIdOrderByRequestedAtDesc(Long expertId);

    // Admin xem theo status (PENDING cần xử lý)
    List<Withdrawal> findByStatusOrderByRequestedAtAsc(WithdrawalStatus status);

    // Tổng số tiền Expert đã rút hoặc đang chờ duyệt (PENDING + APPROVED),
    // dùng để trừ vào số dư khả dụng, tránh rút vượt quá số tiền đã được RELEASED.
    @Query("""
            SELECT COALESCE(SUM(w.amount),0)
            FROM Withdrawal w
            WHERE w.expert.id = :expertId
            AND w.status IN (com.aitasker.payment.enums.WithdrawalStatus.PENDING, com.aitasker.payment.enums.WithdrawalStatus.APPROVED)
            """)
    BigDecimal getReservedOrWithdrawnAmount(@Param("expertId") Long expertId);
}
