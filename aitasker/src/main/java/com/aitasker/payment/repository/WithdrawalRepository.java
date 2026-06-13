package com.aitasker.payment.repository;

import com.aitasker.payment.entity.Withdrawal;
import com.aitasker.payment.enums.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {

    // Expert xem withdrawal của mình
    List<Withdrawal> findByExpertIdOrderByRequestedAtDesc(Long expertId);

    // Admin xem theo status (PENDING cần xử lý)
    List<Withdrawal> findByStatusOrderByRequestedAtAsc(WithdrawalStatus status);
}