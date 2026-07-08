package com.aitasker.payment.service;

import com.aitasker.payment.dto.*;
import com.aitasker.payment.entity.Payment;
import com.aitasker.payment.entity.Transaction;
import java.util.List;

public interface PaymentService {

    // Client nạp tiền vào escrow cho project/milestone
    Payment deposit(DepositRequest request, Long clientId);

    // Client release tiền cho Expert sau khi Milestone APPROVED
    Payment release(ReleaseRequest request, Long clientId);

    // Lấy danh sách giao dịch của một payment
    List<Transaction> getTransactionHistory(Long paymentId);

    // Lấy lịch sử giao dịch của Expert đang đăng nhập
    List<Transaction> getTransactionHistoryForExpert(Long expertId);

    // Lấy tất cả giao dịch (dành cho Admin)
    List<Transaction> getAllTransactions();

    // Hoàn tiền Payment đang HELD (dùng cho Dispute resolution)
    Payment refund(Long paymentId, String reason);
}