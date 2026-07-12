package com.aitasker.dispute.service;

import com.aitasker.audit.service.AuditLogService;
import com.aitasker.common.enums.DisputeStatus;
import com.aitasker.payment.enums.PaymentStatus;
import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.dispute.dto.request.CreateDisputeRequest;
import com.aitasker.dispute.dto.request.ResolveDisputeRequest;
import com.aitasker.dispute.entity.Dispute;
import com.aitasker.dispute.repository.DisputeRepository;
import com.aitasker.exception.BadRequestException;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.milestone.repository.MilestoneRepository;
import com.aitasker.notification.service.NotificationService;
import com.aitasker.payment.entity.Payment;
import com.aitasker.payment.repository.PaymentRepository;
import com.aitasker.payment.service.PaymentService;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisputeServiceTest {

    @Mock private DisputeRepository disputeRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private MilestoneRepository milestoneRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private PaymentService paymentService;
    @Mock private NotificationService notificationService;
    @Mock private AuditLogService auditLogService;

    @InjectMocks
    private DisputeService disputeService;

    private User client;
    private User expert;
    private User stranger;
    private Project project;

    @BeforeEach
    void setUp() {
        client = new User();
        client.setId(1L);
        client.setName("Client One");

        expert = new User();
        expert.setId(2L);

        stranger = new User();
        stranger.setId(3L);

        project = new Project();
        project.setId(10L);
        project.setClient(client);
        project.setExpert(expert);
        project.setStatus(ProjectStatus.ACTIVE);
    }

    @Test
    void create_rejectsWhenCreatorIsNotProjectParticipant() {
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));

        CreateDisputeRequest request = new CreateDisputeRequest();
        request.setProjectId(10L);
        request.setReason("not involved");

        assertThatThrownBy(() -> disputeService.create(request, stranger))
                .isInstanceOf(ForbiddenException.class);

        verify(disputeRepository, never()).save(any());
    }

    @Test
    void create_marksProjectDisputedAndNotifiesOtherParty() {
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(disputeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateDisputeRequest request = new CreateDisputeRequest();
        request.setProjectId(10L);
        request.setReason("Sản phẩm không đúng thỏa thuận");

        disputeService.create(request, client);

        assertThat(project.getStatus()).isEqualTo(ProjectStatus.DISPUTED);
        verify(notificationService).createNotification(eq(2L), anyString(), anyString(), eq("DISPUTE_OPENED"));
    }

    @Test
    void resolve_rejectsWhenDisputeAlreadyResolved() {
        Dispute dispute = Dispute.builder().project(project).creator(client)
                .status(DisputeStatus.RESOLVED_REJECTED).build();
        dispute.setId(20L);
        when(disputeRepository.findById(20L)).thenReturn(Optional.of(dispute));

        ResolveDisputeRequest request = new ResolveDisputeRequest();
        request.setStatus(DisputeStatus.RESOLVED_REFUND);
        request.setResolution("late");

        assertThatThrownBy(() -> disputeService.resolve(20L, request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void resolve_refundsAllHeldPaymentsAndCancelsProject() {
        Dispute dispute = Dispute.builder().project(project).creator(client)
                .status(DisputeStatus.OPEN).build();
        dispute.setId(21L);
        Payment heldPayment = Payment.builder().status(PaymentStatus.HELD)
                .amount(new BigDecimal("400.00")).build();
        heldPayment.setId(99L);

        when(disputeRepository.findById(21L)).thenReturn(Optional.of(dispute));
        when(paymentRepository.findByProjectIdAndStatus(10L, PaymentStatus.HELD)).thenReturn(List.of(heldPayment));
        when(disputeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResolveDisputeRequest request = new ResolveDisputeRequest();
        request.setStatus(DisputeStatus.RESOLVED_REFUND);
        request.setResolution("Hoàn tiền cho Client");

        disputeService.resolve(21L, request);

        verify(paymentService).refund(eq(99L), anyString());
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.CANCELLED);
        assertThat(dispute.getStatus()).isEqualTo(DisputeStatus.RESOLVED_REFUND);
    }
}
