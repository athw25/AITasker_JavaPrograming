package com.aitasker.dispute.service.impl;

import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.common.enums.DisputeStatus;
import com.aitasker.common.enums.Role;
import com.aitasker.dispute.dto.request.AddDisputeMessageRequest;
import com.aitasker.dispute.dto.request.CreateDisputeRequest;
import com.aitasker.dispute.dto.request.DisputeResolveRequest;
import com.aitasker.dispute.dto.response.DisputeResponse;
import com.aitasker.dispute.entity.Dispute;
import com.aitasker.dispute.repository.DisputeRepository;
import com.aitasker.exception.BusinessException;
import com.aitasker.exception.ForbiddenException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisputeServiceImplTest {

    @Mock
    private DisputeRepository disputeRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private DisputeServiceImpl disputeService;

    private User client;
    private User expert;
    private User stranger;
    private Project project;

    @BeforeEach
    void setUp() {
        client = new User();
        client.setId(1L);
        client.setName("Client A");
        client.setRole(Role.CLIENT);

        expert = new User();
        expert.setId(2L);
        expert.setName("Expert B");
        expert.setRole(Role.EXPERT);

        stranger = new User();
        stranger.setId(99L);
        stranger.setRole(Role.CLIENT);

        project = new Project();
        project.setId(100L);
        project.setClient(client);
        project.setExpert(expert);
    }

    @Test
    void createDispute_shouldFail_whenUserIsNotProjectParticipant() {
        CreateDisputeRequest request = new CreateDisputeRequest();
        request.setProjectId(100L);
        request.setReason("Không hài lòng");

        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));

        assertThrows(ForbiddenException.class, () -> disputeService.createDispute(request, stranger));
        verify(disputeRepository, never()).save(any());
    }

    @Test
    void createDispute_shouldFail_whenProjectAlreadyHasOpenDispute() {
        CreateDisputeRequest request = new CreateDisputeRequest();
        request.setProjectId(100L);
        request.setReason("Không hài lòng");

        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));
        when(disputeRepository.existsByProjectIdAndStatusIn(eq(100L), any())).thenReturn(true);

        assertThrows(BusinessException.class, () -> disputeService.createDispute(request, client));
    }

    @Test
    void createDispute_shouldSucceed_whenParticipantAndNoOpenDispute() {
        CreateDisputeRequest request = new CreateDisputeRequest();
        request.setProjectId(100L);
        request.setReason("Không hài lòng với deliverable");

        Dispute saved = Dispute.builder()
                .project(project)
                .createdBy(client)
                .reason(request.getReason())
                .status(DisputeStatus.OPEN)
                .build();
        saved.setId(500L);

        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));
        when(disputeRepository.existsByProjectIdAndStatusIn(eq(100L), any())).thenReturn(false);
        when(disputeRepository.save(any(Dispute.class))).thenReturn(saved);

        DisputeResponse response = disputeService.createDispute(request, client);

        assertEquals(DisputeStatus.OPEN, response.getStatus());
        verify(analyticsService).recordEvent(any(), eq(1L), any(), any(), any());
    }

    @Test
    void resolveDispute_shouldFail_whenCallerIsNotAdmin() {
        DisputeResolveRequest request = new DisputeResolveRequest();
        request.setStatus(DisputeStatus.RESOLVED);
        request.setResolution("Hoàn tiền một phần");

        assertThrows(ForbiddenException.class, () -> disputeService.resolveDispute(500L, request, client));
        verify(disputeRepository, never()).save(any());
    }

    @Test
    void addMessage_shouldFail_whenDisputeAlreadyResolved() {
        Dispute dispute = Dispute.builder()
                .project(project)
                .createdBy(client)
                .reason("Lý do")
                .status(DisputeStatus.RESOLVED)
                .build();
        dispute.setId(500L);

        when(disputeRepository.findById(500L)).thenReturn(Optional.of(dispute));

        AddDisputeMessageRequest request = new AddDisputeMessageRequest();
        request.setMessage("Xin chào");

        assertThrows(BusinessException.class, () -> disputeService.addMessage(500L, request, client));
        verify(disputeRepository, never()).save(any());
    }

    @Test
    void addMessage_shouldSucceed_whenDisputeIsOpenAndSenderIsParticipant() {
        Dispute dispute = Dispute.builder()
                .project(project)
                .createdBy(client)
                .reason("Lý do")
                .status(DisputeStatus.OPEN)
                .build();
        dispute.setId(500L);

        when(disputeRepository.findById(500L)).thenReturn(Optional.of(dispute));
        when(disputeRepository.save(any(Dispute.class))).thenReturn(dispute);

        AddDisputeMessageRequest request = new AddDisputeMessageRequest();
        request.setMessage("Tôi sẽ cung cấp thêm bằng chứng");

        DisputeResponse response = disputeService.addMessage(500L, request, expert);

        assertEquals(1, response.getMessages().size());
        assertEquals("Tôi sẽ cung cấp thêm bằng chứng", response.getMessages().get(0).getMessage());
    }
}
