package com.aitasker.proposal.service;

import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.common.enums.JobStatus;
import com.aitasker.common.enums.ProposalStatus;
import com.aitasker.exception.BusinessException;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.job.entity.JobPost;
import com.aitasker.job.repository.JobPostRepository;
import com.aitasker.notification.service.NotificationService;
import com.aitasker.project.service.ProjectService;
import com.aitasker.proposal.dto.request.ProposalRequestDTO;
import com.aitasker.proposal.entity.Proposal;
import com.aitasker.proposal.repository.ProposalRepository;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
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
class ProposalServiceTest {

    @Mock private ProposalRepository proposalRepository;
    @Mock private JobPostRepository jobPostRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProjectService projectService;
    @Mock private NotificationService notificationService;
    @Mock private AnalyticsService analyticsService;

    @InjectMocks
    private ProposalService proposalService;

    private User client;
    private User expert;
    private JobPost job;

    @BeforeEach
    void setUp() {
        client = new User();
        client.setId(1L);
        client.setName("Client One");

        expert = new User();
        expert.setId(2L);
        expert.setName("Expert One");

        job = new JobPost();
        job.setId(50L);
        job.setTitle("AI Chatbot");
        job.setClient(client);
        job.setStatus(JobStatus.OPEN);
    }

    @Test
    void createProposal_rejectsDuplicateSubmissionFromSameExpert() {
        when(jobPostRepository.findById(50L)).thenReturn(Optional.of(job));
        when(userRepository.findById(2L)).thenReturn(Optional.of(expert));
        when(proposalRepository.existsByJobIdAndExpertId(50L, 2L)).thenReturn(true);

        ProposalRequestDTO request = new ProposalRequestDTO();
        request.setJobId(50L);
        request.setBidAmount(new BigDecimal("1000.00"));
        request.setDuration(10);
        request.setCoverLetter("test");

        assertThatThrownBy(() -> proposalService.createProposal(request, 2L))
                .isInstanceOf(BusinessException.class);

        verify(proposalRepository, never()).save(any());
    }

    @Test
    void acceptProposal_rejectsWhenCallerIsNotJobOwner() {
        Proposal proposal = new Proposal();
        proposal.setId(200L);
        proposal.setJob(job);
        proposal.setExpert(expert);
        proposal.setStatus(ProposalStatus.PENDING);
        when(proposalRepository.findById(200L)).thenReturn(Optional.of(proposal));

        assertThatThrownBy(() -> proposalService.acceptProposal(200L, 999L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void acceptProposal_rejectsWhenProposalAlreadyProcessed() {
        Proposal proposal = new Proposal();
        proposal.setId(201L);
        proposal.setJob(job);
        proposal.setExpert(expert);
        proposal.setStatus(ProposalStatus.ACCEPTED);
        when(proposalRepository.findById(201L)).thenReturn(Optional.of(proposal));

        assertThatThrownBy(() -> proposalService.acceptProposal(201L, 1L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void acceptProposal_marksAcceptedAndCreatesProject() {
        Proposal proposal = new Proposal();
        proposal.setId(202L);
        proposal.setJob(job);
        proposal.setExpert(expert);
        proposal.setStatus(ProposalStatus.PENDING);
        when(proposalRepository.findById(202L)).thenReturn(Optional.of(proposal));

        proposalService.acceptProposal(202L, 1L);

        assertThat(proposal.getStatus()).isEqualTo(ProposalStatus.ACCEPTED);
        assertThat(job.getStatus()).isEqualTo(JobStatus.IN_PROGRESS);
        verify(projectService).createProjectFromProposal(proposal);
    }
}
