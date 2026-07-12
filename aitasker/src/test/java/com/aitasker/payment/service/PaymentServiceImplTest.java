package com.aitasker.payment.service;

import com.aitasker.audit.service.AuditLogService;
import com.aitasker.common.enums.MilestoneStatus;
import com.aitasker.exception.BadRequestException;
import com.aitasker.milestone.entity.Milestone;
import com.aitasker.milestone.repository.MilestoneRepository;
import com.aitasker.payment.dto.DepositRequest;
import com.aitasker.payment.dto.ReleaseRequest;
import com.aitasker.payment.entity.Payment;
import com.aitasker.payment.enums.PaymentStatus;
import com.aitasker.payment.repository.PaymentRepository;
import com.aitasker.payment.repository.TransactionRepository;
import com.aitasker.project.entity.Project;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private MilestoneRepository milestoneRepository;
    @Mock private AuditLogService auditLogService;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setMilestones(List.of());
    }

    @Test
    void deposit_createsHeldPaymentAndDepositTransaction() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DepositRequest request = new DepositRequest();
        request.setProjectId(1L);
        request.setAmount(new BigDecimal("500.00"));

        Payment payment = paymentService.deposit(request, 10L);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.HELD);
        assertThat(payment.getAmount()).isEqualByComparingTo("500.00");
        verify(transactionRepository).save(any());
    }

    @Test
    void release_failsWhenPaymentNotHeld() {
        Payment payment = Payment.builder().status(PaymentStatus.RELEASED)
                .amount(new BigDecimal("100.00")).build();
        payment.setId(1L);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        ReleaseRequest request = new ReleaseRequest();
        request.setPaymentId(1L);

        assertThatThrownBy(() -> paymentService.release(request, 10L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void release_syncsLinkedMilestoneToPaidAndCompletesProjectWhenAllPaid() {
        Milestone milestone = new Milestone();
        milestone.setId(5L);
        milestone.setStatus(MilestoneStatus.APPROVED);
        milestone.setProject(project);
        project.setMilestones(List.of(milestone));

        Payment payment = Payment.builder().status(PaymentStatus.HELD)
                .milestone(milestone).amount(new BigDecimal("100.00")).build();
        payment.setId(1L);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReleaseRequest request = new ReleaseRequest();
        request.setPaymentId(1L);

        Payment result = paymentService.release(request, 10L);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.RELEASED);
        assertThat(milestone.getStatus()).isEqualTo(MilestoneStatus.PAID);
        verify(milestoneRepository).save(milestone);
        verify(projectRepository).save(project);
    }

    @Test
    void refund_onlyAllowedWhenPaymentIsHeld() {
        Payment payment = Payment.builder().status(PaymentStatus.PENDING)
                .amount(new BigDecimal("100.00")).build();
        payment.setId(2L);
        when(paymentRepository.findById(2L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.refund(2L, "test"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void refund_marksPaymentRefundedAndLogsTransaction() {
        Payment payment = Payment.builder().status(PaymentStatus.HELD)
                .amount(new BigDecimal("250.00")).build();
        payment.setId(3L);
        when(paymentRepository.findById(3L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentService.refund(3L, "Dispute resolved");

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        verify(transactionRepository).save(any());
    }
}
