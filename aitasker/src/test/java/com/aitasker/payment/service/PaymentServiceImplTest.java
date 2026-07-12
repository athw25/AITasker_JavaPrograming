package com.aitasker.payment.service;

import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.common.enums.MilestoneStatus;
import com.aitasker.common.enums.Role;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.milestone.entity.Milestone;
import com.aitasker.milestone.repository.MilestoneRepository;
import com.aitasker.notification.service.NotificationService;
import com.aitasker.payment.dto.DepositRequest;
import com.aitasker.payment.dto.ReleaseRequest;
import com.aitasker.payment.entity.Payment;
import com.aitasker.payment.entity.Transaction;
import com.aitasker.payment.enums.PaymentStatus;
import com.aitasker.payment.repository.PaymentRepository;
import com.aitasker.payment.repository.TransactionRepository;
import com.aitasker.project.entity.Project;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
    @Mock private AnalyticsService analyticsService;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User owner;
    private User stranger;
    private User expert;
    private Project project;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setRole(Role.CLIENT);

        stranger = new User();
        stranger.setId(2L);
        stranger.setRole(Role.CLIENT);

        expert = new User();
        expert.setId(3L);
        expert.setRole(Role.EXPERT);

        project = Project.builder()
                .client(owner)
                .expert(expert)
                .status(com.aitasker.common.enums.ProjectStatus.ACTIVE)
                .build();
        project.setId(10L);
    }

    @Test
    void deposit_khongPhaiChuProject_biTuChoi() {
        DepositRequest request = new DepositRequest();
        request.setProjectId(10L);
        request.setAmount(BigDecimal.valueOf(500));

        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> paymentService.deposit(request, stranger.getId()))
                .isInstanceOf(ForbiddenException.class);

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void deposit_dungChuProject_thanhCong() {
        DepositRequest request = new DepositRequest();
        request.setProjectId(10L);
        request.setAmount(BigDecimal.valueOf(500));

        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment payment = paymentService.deposit(request, owner.getId());

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.HELD);
        verify(transactionRepository).save(any(Transaction.class));
        verify(analyticsService).recordEvent(any(), eq(owner.getId()), any(), any(), any());
    }

    @Test
    void release_khongPhaiChuProject_biTuChoi() {
        Milestone milestone = new Milestone();
        milestone.setId(20L);
        milestone.setStatus(MilestoneStatus.APPROVED);

        Payment payment = Payment.builder()
                .project(project)
                .milestone(milestone)
                .amount(BigDecimal.valueOf(500))
                .status(PaymentStatus.HELD)
                .build();
        payment.setId(30L);

        ReleaseRequest request = new ReleaseRequest();
        request.setPaymentId(30L);

        when(paymentRepository.findById(30L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.release(request, stranger.getId()))
                .isInstanceOf(ForbiddenException.class);

        verify(notificationService, never()).createNotification(any(), any(), any(), any());
    }

    @Test
    void release_milestoneChuaApproved_biTuChoi() {
        Milestone milestone = new Milestone();
        milestone.setId(20L);
        milestone.setStatus(MilestoneStatus.SUBMITTED);

        Payment payment = Payment.builder()
                .project(project)
                .milestone(milestone)
                .amount(BigDecimal.valueOf(500))
                .status(PaymentStatus.HELD)
                .build();
        payment.setId(30L);

        ReleaseRequest request = new ReleaseRequest();
        request.setPaymentId(30L);

        when(paymentRepository.findById(30L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.release(request, owner.getId()))
                .isInstanceOf(com.aitasker.exception.BadRequestException.class);
    }

    @Test
    void getTransactionHistory_nguoiNgoaiCuoc_biTuChoi() {
        Payment payment = Payment.builder().project(project).build();
        payment.setId(30L);
        when(paymentRepository.findById(30L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.getTransactionHistory(30L, stranger.getId(), false))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void getTransactionHistory_admin_luonXemDuoc() {
        Payment payment = Payment.builder().project(project).build();
        payment.setId(30L);
        when(paymentRepository.findById(30L)).thenReturn(Optional.of(payment));
        when(transactionRepository.findByPaymentIdOrderByCreatedAtDesc(30L)).thenReturn(java.util.List.of());

        paymentService.getTransactionHistory(30L, stranger.getId(), true);

        verify(transactionRepository).findByPaymentIdOrderByCreatedAtDesc(30L);
    }
}
