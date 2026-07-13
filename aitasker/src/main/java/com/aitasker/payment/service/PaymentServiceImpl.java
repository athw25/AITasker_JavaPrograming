package com.aitasker.payment.service;

import com.aitasker.analytics.enums.AnalyticsEventType;
import com.aitasker.analytics.service.AnalyticsService;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aitasker.exception.BadRequestException;
import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.milestone.entity.Milestone;
import com.aitasker.milestone.repository.MilestoneRepository;
import com.aitasker.payment.dto.DepositRequest;
import com.aitasker.payment.dto.ReleaseRequest;
import com.aitasker.payment.entity.Payment;
import com.aitasker.payment.entity.Transaction;
import com.aitasker.payment.enums.PaymentStatus;
import com.aitasker.payment.enums.TransactionType;
import com.aitasker.payment.repository.PaymentRepository;
import com.aitasker.payment.repository.TransactionRepository;
import com.aitasker.project.entity.Project;
import com.aitasker.project.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final AnalyticsService analyticsService;

    @Override
    public Payment deposit(DepositRequest request, Long clientId) {
        // 1. Kiểm tra project tồn tại
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project không tìm thấy"));

        // 2. Kiểm tra Milestone nếu có
        Milestone milestone = null;
        if (request.getMilestoneId() != null) {
            milestone = milestoneRepository.findById(request.getMilestoneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Milestone không tìm thấy"));
        }

        // 3. Tạo Payment với status HELD (tiền đang giữ trong escrow)
        Payment payment = Payment.builder()
                .project(project)
                .milestone(milestone)
                .amount(request.getAmount())
                .status(PaymentStatus.HELD)
                .transactionRef("TXN-" + System.currentTimeMillis())
                .build();
        paymentRepository.save(payment);

        // 4. Ghi Transaction — mọi giao dịch đều phải lưu lại
        Transaction transaction = Transaction.builder()
                .payment(payment)
                .type(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .description("Deposit vào Escrow cho Project #" + project.getId())
                .build();
        transactionRepository.save(transaction);

        analyticsService.recordEvent(AnalyticsEventType.PAYMENT_DEPOSITED, clientId, "CLIENT",
                "PROJECT", String.valueOf(project.getId()));

        return payment;
    }

    @Override
    public Payment release(ReleaseRequest request, Long clientId) {
        // 1. Tìm Payment
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment không tìm thấy"));

        // 2. Chỉ release khi đang HELD
        if (payment.getStatus() != PaymentStatus.HELD) {
            throw new BadRequestException("Payment phải ở trạng thái HELD mới có thể release");
        }

        // 3. Nếu có Milestone → kiểm tra Milestone phải APPROVED
        if (payment.getMilestone() != null) {
            Milestone milestone = payment.getMilestone();
            if (milestone.getStatus() != com.aitasker.common.enums.MilestoneStatus.APPROVED) {
                throw new BadRequestException("Milestone phải được APPROVED trước khi release tiền");
            }
        }

        // 4. Cập nhật trạng thái Payment
        payment.setStatus(PaymentStatus.RELEASED);
        paymentRepository.save(payment);

        // 5. Ghi Transaction RELEASE
        Transaction transaction = Transaction.builder()
                .payment(payment)
                .type(TransactionType.RELEASE)
                .amount(payment.getAmount())
                .description("Release Escrow cho Expert — Payment #" + payment.getId())
                .build();
        transactionRepository.save(transaction);

        analyticsService.recordEvent(AnalyticsEventType.PAYMENT_RELEASED, clientId, "CLIENT",
                "PAYMENT", String.valueOf(payment.getId()));

        return payment;
    }
    @Override
    public Payment refund(Long paymentId, java.math.BigDecimal amount, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment không tìm thấy"));

        if (payment.getStatus() != PaymentStatus.HELD) {
            throw new BadRequestException(
                "Chỉ refund khi Payment đang HELD. Hiện tại: " + payment.getStatus());
    }
        if (amount.compareTo(java.math.BigDecimal.ZERO) <= 0
                || amount.compareTo(payment.getAmount()) > 0) {
            throw new BadRequestException("Số tiền hoàn không hợp lệ. Tối đa: " + payment.getAmount());
    }

    payment.setStatus(PaymentStatus.REFUNDED);
    paymentRepository.save(payment);
    Transaction transaction = Transaction.builder()
                .payment(payment)
                .type(TransactionType.REFUND)
                .amount(amount)
                .description(reason)
                .build();
    transactionRepository.save(transaction);
    analyticsService.recordEvent(AnalyticsEventType.PAYMENT_REFUNDED, null, null,
            "PAYMENT", String.valueOf(payment.getId()));
    return payment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionHistory(Long paymentId) {
        return transactionRepository.findByPaymentIdOrderByCreatedAtDesc(paymentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getMyTransactionHistory(Long userId) {
        return transactionRepository.findByPayment_Project_Client_IdOrPayment_Project_Expert_IdOrderByCreatedAtDesc(userId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}