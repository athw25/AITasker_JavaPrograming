package com.aitasker.payment.service;

import com.aitasker.analytics.enums.AnalyticsEventType;
import com.aitasker.analytics.service.AnalyticsService;
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
    private final AnalyticsService analyticsService;

    // Expert tạo yêu cầu rút tiền
    public Withdrawal requestWithdrawal(WithdrawalRequest request, Long expertId) {
        User expert = userRepository.findById(expertId)
                .orElseThrow(() -> new ResourceNotFoundException("Expert không tìm thấy"));

        BigDecimal amount = request.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Số tiền rút phải lớn hơn 0");
        }

        BigDecimal availableBalance = getAvailableBalance(expertId);
        if (amount.compareTo(availableBalance) > 0) {
            throw new BadRequestException(
                    "Số tiền rút vượt quá số dư khả dụng. Số dư hiện tại: " + availableBalance);
        }

        Withdrawal withdrawal = Withdrawal.builder()
                .expert(expert)
                .amount(amount)
                .status(WithdrawalStatus.PENDING)
                .build();
        Withdrawal saved = withdrawalRepository.save(withdrawal);
        analyticsService.recordEvent(AnalyticsEventType.WITHDRAWAL_REQUESTED, expertId, "EXPERT",
                "WITHDRAWAL", String.valueOf(saved.getId()));
        return saved;
    }

    // Số dư khả dụng = Tổng tiền đã RELEASED cho Expert - Tổng tiền đang PENDING/đã APPROVED rút
    @Transactional(readOnly = true)
    public BigDecimal getAvailableBalance(Long expertId) {
        BigDecimal released = paymentRepository.getReleasedAmountForExpert(expertId);
        BigDecimal reserved = withdrawalRepository.getReservedOrWithdrawnAmount(expertId);
        return released.subtract(reserved);
    }

    // Admin duyệt withdrawal
    public Withdrawal approveWithdrawal(Long withdrawalId) {
        Withdrawal withdrawal = withdrawalRepository.findById(withdrawalId)
                .orElseThrow(() -> new ResourceNotFoundException("Withdrawal không tìm thấy"));

        if (withdrawal.getStatus() != WithdrawalStatus.PENDING) {
            throw new BadRequestException("Withdrawal không ở trạng thái PENDING");
        }

        // Đối chiếu lại số dư tại thời điểm duyệt để tránh trường hợp Expert đã bị
        // trừ số dư bởi các Withdrawal khác được duyệt trước đó.
        BigDecimal releasedExcludingThis = paymentRepository.getReleasedAmountForExpert(withdrawal.getExpert().getId());
        BigDecimal reservedExcludingThis = withdrawalRepository
                .getReservedOrWithdrawnAmount(withdrawal.getExpert().getId())
                .subtract(withdrawal.getAmount());
        BigDecimal availableExcludingThis = releasedExcludingThis.subtract(reservedExcludingThis);
        if (withdrawal.getAmount().compareTo(availableExcludingThis) > 0) {
            throw new BadRequestException("Số dư của Expert không đủ để duyệt yêu cầu rút tiền này");
        }

        withdrawal.setStatus(WithdrawalStatus.APPROVED);
        withdrawal.setProcessedAt(LocalDateTime.now());

    Transaction transaction = Transaction.builder()
            .payment(null)
            .type(TransactionType.WITHDRAWAL)
            .amount(withdrawal.getAmount())
            .description("Withdrawal #" + withdrawalId
                + " được duyệt — Expert #" + withdrawal.getExpert().getId())
            .build();
    transactionRepository.save(transaction);

        Withdrawal approved = withdrawalRepository.save(withdrawal);
        analyticsService.recordEvent(AnalyticsEventType.WITHDRAWAL_APPROVED, withdrawal.getExpert().getId(),
                "EXPERT", "WITHDRAWAL", String.valueOf(withdrawalId));
        return approved;
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