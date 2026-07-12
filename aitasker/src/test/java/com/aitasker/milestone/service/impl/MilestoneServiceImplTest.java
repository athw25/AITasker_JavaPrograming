package com.aitasker.milestone.service.impl;

import com.aitasker.common.enums.MilestoneStatus;
import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.delivery.repository.DeliveryRepository;
import com.aitasker.delivery.service.DeliveryService;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.milestone.entity.Milestone;
import com.aitasker.milestone.exception.InvalidMilestoneStateException;
import com.aitasker.milestone.mapper.MilestoneMapper;
import com.aitasker.milestone.repository.MilestoneRepository;
import com.aitasker.payment.entity.Payment;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MilestoneServiceImplTest {

    @Mock private MilestoneRepository milestoneRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private DeliveryRepository deliveryRepository;
    @Mock private DeliveryService deliveryService;
    @Mock private MilestoneMapper milestoneMapper;
    @Mock private PaymentRepository paymentRepository;
    @Mock private TransactionRepository transactionRepository;

    @InjectMocks
    private MilestoneServiceImpl milestoneService;

    private User client;
    private Project project;
    private Milestone milestone;

    @BeforeEach
    void setUp() {
        client = new User();
        client.setId(1L);

        project = new Project();
        project.setId(10L);
        project.setClient(client);
        project.setStatus(ProjectStatus.ACTIVE);

        milestone = new Milestone();
        milestone.setId(100L);
        milestone.setProject(project);
        milestone.setStatus(MilestoneStatus.APPROVED);
        milestone.setAmount(new BigDecimal("300.00"));
        project.setMilestones(List.of(milestone));

        when(milestoneRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(milestone));
    }

    @Test
    void releasePayment_rejectsWhenCallerIsNotProjectClient() {
        User stranger = new User();
        stranger.setId(999L);

        assertThatThrownBy(() -> milestoneService.releasePayment(100L, stranger))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void releasePayment_rejectsWhenNoDepositExists() {
        when(paymentRepository.findByMilestoneId(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> milestoneService.releasePayment(100L, client))
                .isInstanceOf(InvalidMilestoneStateException.class);
    }

    @Test
    void releasePayment_releasesHeldPaymentAndMarksMilestonePaid() {
        Payment payment = Payment.builder().status(PaymentStatus.HELD)
                .amount(new BigDecimal("300.00")).build();
        payment.setId(500L);
        when(paymentRepository.findByMilestoneId(100L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(milestoneMapper.toResponse(any())).thenReturn(null);

        milestoneService.releasePayment(100L, client);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.RELEASED);
        assertThat(milestone.getStatus()).isEqualTo(MilestoneStatus.PAID);
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.COMPLETED);
        verify(transactionRepository).save(any());
    }

    @Test
    void releasePayment_rejectsWhenMilestoneNotApproved() {
        milestone.setStatus(MilestoneStatus.SUBMITTED);

        assertThatThrownBy(() -> milestoneService.releasePayment(100L, client))
                .isInstanceOf(InvalidMilestoneStateException.class);
    }
}
