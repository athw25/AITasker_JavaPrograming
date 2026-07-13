package com.aitasker.payment.repository;

import com.aitasker.payment.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Lịch sử giao dịch theo payment
    List<Transaction> findByPaymentIdOrderByCreatedAtDesc(Long paymentId);

    // Lịch sử giao dịch theo project (join qua payment)
    List<Transaction> findByPaymentProjectIdOrderByCreatedAtDesc(Long projectId);

    // Lịch sử giao dịch của chính user hiện tại (Client hoặc Expert trong project của Payment)
    List<Transaction> findByPayment_Project_Client_IdOrPayment_Project_Expert_IdOrderByCreatedAtDesc(
            Long clientId, Long expertId);
}