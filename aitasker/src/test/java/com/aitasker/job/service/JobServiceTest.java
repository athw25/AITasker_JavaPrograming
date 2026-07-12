package com.aitasker.job.service;

import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.common.enums.JobStatus;
import com.aitasker.common.enums.Role;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.job.dto.JobPostRequest;
import com.aitasker.job.dto.JobPostResponse;
import com.aitasker.job.entity.JobPost;
import com.aitasker.job.repository.JobPostRepository;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobPostRepository jobPostRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private JobService jobService;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;
    private User owner;
    private User otherClient;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");
        owner.setRole(Role.CLIENT);

        otherClient = new User();
        otherClient.setId(2L);
        otherClient.setEmail("other@example.com");
        otherClient.setRole(Role.CLIENT);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = new UsernamePasswordAuthenticationToken(owner.getEmail(), null);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    @AfterEach
    void tearDown() {
        securityContextHolderMock.close();
    }

    @Test
    void create_shouldPersistAllRequestFields() {
        JobPostRequest request = new JobPostRequest();
        request.setTitle("Facebook Sales Chatbot");
        request.setDescription("Build an AI chatbot");
        request.setBudget(new BigDecimal("3000"));
        request.setDeadline(LocalDate.now().plusDays(30));
        request.setRequiredSkills("Python, LangChain");

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(jobPostRepository.save(any(JobPost.class))).thenAnswer(invocation -> {
            JobPost job = invocation.getArgument(0);
            job.setId(500L);
            return job;
        });

        JobPostResponse response = jobService.create(request);

        assertEquals("Facebook Sales Chatbot", response.getTitle());
        assertEquals(request.getDeadline(), response.getDeadline());
        assertEquals("Python, LangChain", response.getRequiredSkills());
        verify(analyticsService).recordEvent(any(), eq(1L), any(), any(), any());
    }

    @Test
    void getAll_shouldReturnPaginatedResult() {
        JobPost job = new JobPost();
        job.setId(1L);
        job.setTitle("Job 1");

        Page<JobPost> page = new PageImpl<>(List.of(job), PageRequest.of(0, 10), 1);
        when(jobPostRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        var result = jobService.getAll(0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void update_shouldThrowForbidden_whenCallerIsNotJobOwner() {
        JobPost job = new JobPost();
        job.setId(10L);
        job.setClient(owner);
        job.setStatus(JobStatus.OPEN);

        // Người gọi hiện tại (theo SecurityContext) là "owner@example.com" nhưng ta
        // giả lập userRepository trả về otherClient để mô phỏng người dùng khác đăng nhập.
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(otherClient));
        when(jobPostRepository.findById(10L)).thenReturn(Optional.of(job));

        JobPostRequest request = new JobPostRequest();
        request.setTitle("Hacked title");
        request.setDescription("desc");
        request.setBudget(new BigDecimal("100"));
        request.setDeadline(LocalDate.now().plusDays(1));
        request.setRequiredSkills("skill");

        assertThrows(ForbiddenException.class, () -> jobService.update(10L, request));
        verify(jobPostRepository, never()).save(any());
    }

    @Test
    void update_shouldSucceed_whenCallerIsJobOwner() {
        JobPost job = new JobPost();
        job.setId(10L);
        job.setClient(owner);
        job.setStatus(JobStatus.OPEN);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(jobPostRepository.findById(10L)).thenReturn(Optional.of(job));
        when(jobPostRepository.save(any(JobPost.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobPostRequest request = new JobPostRequest();
        request.setTitle("Updated title");
        request.setDescription("desc");
        request.setBudget(new BigDecimal("100"));
        request.setDeadline(LocalDate.now().plusDays(1));
        request.setRequiredSkills("skill");

        JobPostResponse response = jobService.update(10L, request);

        assertEquals("Updated title", response.getTitle());
    }
}
