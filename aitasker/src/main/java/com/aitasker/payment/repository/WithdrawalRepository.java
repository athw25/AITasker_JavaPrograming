package com.aitasker.payment.repository;

import com.aitasker.payment.entity.Withdrawal;
import com.aitasker.payment.enums.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {

    // Expert xem withdrawal của mình
    List<Withdrawal> findByExpertIdOrderByRequestedAtDesc(Long expertId);

    // Admin xem theo status (PENDING cần xử lý)
    List<Withdrawal> findByStatusOrderByRequestedAtAsc(WithdrawalStatus status);

    @Query("SELECT COALESCE(SUM(w.amount), 0) FROM Withdrawal w WHERE w.expert.id = :expertId AND w.status IN :statuses")
    BigDecimal sumAmountByExpertIdAndStatusIn(@Param("expertId") Long expertId, @Param("statuses") Collection<WithdrawalStatus> statuses);
}