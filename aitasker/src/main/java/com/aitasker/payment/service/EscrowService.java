package com.aitasker.payment.service;

import com.aitasker.exception.BadRequestException;
import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.payment.dto.WithdrawalRequest;
import com.aitasker.payment.entity.*;
import com.aitasker.payment.enums.*;
import com.aitasker.payment.repository.*;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EscrowService {

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final UserRepository userRepository;

    // Expert tạo yêu cầu rút tiền
    public Withdrawal requestWithdrawal(WithdrawalRequest request, Long expertId) {
        User expert = userRepository.findById(expertId)
                .orElseThrow(() -> new ResourceNotFoundException("Expert không tìm thấy"));

        // Tính tổng tiền đã được RELEASED cho expert này
        // (trong thực tế sẽ có thêm logic kiểm tra số dư)
        BigDecimal amount = request.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Số tiền rút phải lớn hơn 0");
        }

        Withdrawal withdrawal = Withdrawal.builder()
                .expert(expert)
                .amount(amount)
                .status(WithdrawalStatus.PENDING)
                .build();
        return withdrawalRepository.save(withdrawal);
    }

    // Admin duyệt withdrawal
    public Withdrawal approveWithdrawal(Long withdrawalId) {
        Withdrawal withdrawal = withdrawalRepository.findById(withdrawalId)
                .orElseThrow(() -> new ResourceNotFoundException("Withdrawal không tìm thấy"));

        if (withdrawal.getStatus() != WithdrawalStatus.PENDING) {
            throw new BadRequestException("Withdrawal không ở trạng thái PENDING");
        }

        withdrawal.setStatus(WithdrawalStatus.APPROVED);
        withdrawal.setProcessedAt(LocalDateTime.now());

        // Ghi Transaction WITHDRAWAL
        // Cần payment tham chiếu — có thể dùng payment gần nhất của expert
        // hoặc tạo payment riêng cho withdrawal (tùy kiến trúc nhóm)

        return withdrawalRepository.save(withdrawal);
    }

    // Lấy danh sách tất cả transaction (cho Admin)
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionHistory() {
        return transactionRepository.findAll();
    }

    // Lấy danh sách withdrawal theo status (cho Admin)
    @Transactional(readOnly = true)
    public List<Withdrawal> getWithdrawalsByStatus(WithdrawalStatus status) {
        return withdrawalRepository.findByStatusOrderByRequestedAtAsc(status);
    }

    // Expert xem withdrawal của mình
    @Transactional(readOnly = true)
    public List<Withdrawal> getMyWithdrawals(Long expertId) {
        return withdrawalRepository.findByExpertIdOrderByRequestedAtDesc(expertId);
    }
}