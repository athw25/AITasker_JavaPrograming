package com.aitasker.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aitasker.analytics.enums.AnalyticsEventType;
import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.exception.BadRequestException;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.milestone.entity.Milestone;
import com.aitasker.milestone.repository.MilestoneRepository;
import com.aitasker.notification.service.NotificationService;
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
import com.aitasker.security.audit.enums.AuditAction;
import com.aitasker.security.audit.service.AuditLogService;
import com.aitasker.user.entity.User;

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
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Override
    public Payment deposit(DepositRequest request, Long clientId) {
        // 1. Kiểm tra project tồn tại
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project không tìm thấy"));

        // 1b. Chỉ Client sở hữu Project mới được nạp tiền vào Escrow của Project đó
        assertProjectClient(project, clientId);

        // 2. Kiểm tra Milestone nếu có
        Milestone milestone = null;
        if (request.getMilestoneId() != null) {
            milestone = milestoneRepository.findById(request.getMilestoneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Milestone không tìm thấy"));
            if (!milestone.getProject().getId().equals(project.getId())) {
                throw new BadRequestException("Milestone không thuộc Project đã chọn");
            }
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

        analyticsService.recordEvent(AnalyticsEventType.PAYMENT_DEPOSITED, clientId,
                "CLIENT", "Payment", payment.getId().toString());
        auditLogService.log(AuditAction.PAYMENT_DEPOSITED, clientId, null,
                "Payment", payment.getId().toString(),
                "Deposit " + request.getAmount() + " vào Escrow cho Project #" + project.getId());

        return payment;
    }

    @Override
    public Payment release(ReleaseRequest request, Long clientId) {
        // 1. Tìm Payment
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment không tìm thấy"));

        // 1b. Chỉ Client sở hữu Project mới được giải ngân Payment đó
        assertProjectClient(payment.getProject(), clientId);

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

        analyticsService.recordEvent(AnalyticsEventType.PAYMENT_RELEASED, clientId,
                "CLIENT", "Payment", payment.getId().toString());
        auditLogService.log(AuditAction.PAYMENT_RELEASED, clientId, null,
                "Payment", payment.getId().toString(),
                "Release " + payment.getAmount() + " cho Payment #" + payment.getId());

        User expert = payment.getProject().getExpert();
        if (expert != null) {
            notificationService.createNotification(
                    expert.getId(),
                    "Thanh toán đã được giải ngân",
                    "Client đã release " + payment.getAmount() + " cho Payment #" + payment.getId()
                            + " của Project #" + payment.getProject().getId() + ".",
                    "PAYMENT_RELEASED"
            );
        }

        return payment;
    }

    /**
     * Chỉ Client sở hữu Project (hoặc chủ Payment) mới được thao tác Escrow của Project đó.
     * Trước bản vá này, deposit/release không kiểm tra sở hữu Project (lỗi IDOR).
     */
    private void assertProjectClient(Project project, Long clientId) {
        if (project.getClient() == null || !project.getClient().getId().equals(clientId)) {
            throw new ForbiddenException("Bạn không có quyền thao tác Escrow của Project này.");
        }
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
    analyticsService.recordEvent(AnalyticsEventType.PAYMENT_REFUNDED, null,
            "ADMIN", "Payment", payment.getId().toString());
    auditLogService.log(AuditAction.PAYMENT_REFUNDED, null, null,
            "Payment", payment.getId().toString(), "Refund " + amount + " — Lý do: " + reason);
    return payment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionHistory(Long paymentId, Long requesterId, boolean isAdmin) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment không tìm thấy"));

        if (!isAdmin) {
            Project project = payment.getProject();
            boolean isParticipant = (project.getClient() != null && project.getClient().getId().equals(requesterId))
                    || (project.getExpert() != null && project.getExpert().getId().equals(requesterId));
            if (!isParticipant) {
                throw new ForbiddenException("Bạn không có quyền xem giao dịch của Payment này.");
            }
        }

        return transactionRepository.findByPaymentIdOrderByCreatedAtDesc(paymentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}