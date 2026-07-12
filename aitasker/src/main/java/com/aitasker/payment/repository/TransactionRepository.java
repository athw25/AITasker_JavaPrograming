// TransactionRepository.java
package com.aitasker.payment.repository;

import com.aitasker.payment.entity.Transaction;
import com.aitasker.payment.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Lịch sử giao dịch theo payment
    List<Transaction> findByPaymentIdOrderByCreatedAtDesc(Long paymentId);

    // Lịch sử giao dịch theo project (join qua payment)
    List<Transaction> findByPaymentProjectIdOrderByCreatedAtDesc(Long projectId);

    @Query("SELECT t FROM Transaction t JOIN t.payment p JOIN p.project pr WHERE pr.expert.id = :expertId ORDER BY t.createdAt DESC")
    List<Transaction> findByPaymentProjectExpertIdOrderByCreatedAtDesc(@Param("expertId") Long expertId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = :type")
    BigDecimal sumAmountByType(@Param("type") TransactionType type);
}