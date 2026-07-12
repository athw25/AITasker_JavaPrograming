package com.aitasker.payment.service;

import java.util.List;

import com.aitasker.payment.dto.DepositRequest;
import com.aitasker.payment.dto.ReleaseRequest;
import com.aitasker.payment.entity.Payment;
import com.aitasker.payment.entity.Transaction;

public interface PaymentService {
    Payment deposit(DepositRequest request, Long clientId);
    Payment release(ReleaseRequest request, Long clientId);
    List<Transaction> getTransactionHistory(Long paymentId, Long requesterId, boolean isAdmin);
    List<Transaction> getAllTransactions();
    Payment refund(Long paymentId, java.math.BigDecimal amount, String reason);
}