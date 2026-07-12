package com.aitasker.dispute.service.impl;

import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.common.enums.Role;
import com.aitasker.expert.entity.ExpertProfile;
import com.aitasker.expert.repository.ExpertProfileRepository;
import com.aitasker.expert.repository.PortfolioRepository;
import com.aitasker.job.entity.JobPost;
import com.aitasker.job.repository.JobPostRepository;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.proposal.repository.ProposalRepository;
import com.aitasker.recommendation.dto.response.RecommendationResponseDTO;
import com.aitasker.recommendation.repository.RecommendationRepository;
import com.aitasker.review.repository.ReviewRepository;
import com.aitasker.recommendation.service.RecommendationService;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock private RecommendationRepository recommendationRepository;
    @Mock private JobPostRepository jobPostRepository;
    @Mock private UserRepository userRepository;
    @Mock private ExpertProfileRepository expertProfileRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private PortfolioRepository portfolioRepository;
    @Mock private ProposalRepository proposalRepository;
    @Mock private AnalyticsService analyticsService;

    @InjectMocks
    private RecommendationService recommendationService;

    private User client;
    private User expert;
    private JobPost job;
    private ExpertProfile profile;

    @BeforeEach
    void setUp() {
        client = new User();
        client.setId(1L);
        client.setRole(Role.CLIENT);
        client.setEmail("client@aitasker.com");

        expert = new User();
        expert.setId(2L);
        expert.setRole(Role.EXPERT);

        job = new JobPost();
        job.setId(5L);
        job.setClient(client);
        job.setRequiredSkills("Java, Spring Boot");

        profile = new ExpertProfile();
        profile.setUser(expert);
        profile.setSkills("Java, Spring Boot, Docker");
        profile.setExperienceYears(3);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(client.getEmail(), null, List.of()));
        when(userRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void recommendExpertsForJob_dungDuLieuThatThayViMacDinh() {
        when(jobPostRepository.findById(5L)).thenReturn(Optional.of(job));
        when(expertProfileRepository.findByRoleWithUser(Role.EXPERT)).thenReturn(List.of(profile));

        // Expert có rating trung bình 4.5/5, 10 project (9 hoàn thành), 3 portfolio
        when(reviewRepository.getAverageRatingsForExperts())
                .thenReturn(Collections.singletonList(new Object[]{2L, 4.5}));
        when(projectRepository.countTotalProjectsGroupByExpert())
                .thenReturn(Collections.singletonList(new Object[]{2L, 10L}));
        when(projectRepository.countCompletedProjectsGroupByExpert())
                .thenReturn(Collections.singletonList(new Object[]{2L, 9L}));
        when(portfolioRepository.getPortfolioCountsForExperts())
                .thenReturn(Collections.singletonList(new Object[]{2L, 3L}));

        when(recommendationRepository.findByJobIdAndExpertId(5L, 2L)).thenReturn(Optional.empty());
        when(recommendationRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        List<RecommendationResponseDTO> results = recommendationService.recommendExpertsForJob(5L);

        assertThat(results).hasSize(1);
        RecommendationResponseDTO dto = results.get(0);

        // Rating 4.5/5 -> 90 điểm (không còn là giá trị mặc định 80)
        assertThat(dto.getRatingScore()).isEqualTo(90.0);
        // 9/10 hoàn thành -> 90 điểm (không còn là giá trị mặc định 100)
        assertThat(dto.getSuccessRateScore()).isEqualTo(90.0);
        // 3 portfolio -> 60 điểm (không còn là giá trị mặc định 0)
        assertThat(dto.getPortfolioScore()).isEqualTo(60.0);

        verify(analyticsService).recordEvent(any(), eq(expert.getId()), any(), any(), any());
    }

    @Test
    void recommendExpertsForJob_expertChuaCoDuLieu_dungGiaTriMacDinh() {
        when(jobPostRepository.findById(5L)).thenReturn(Optional.of(job));
        when(expertProfileRepository.findByRoleWithUser(Role.EXPERT)).thenReturn(List.of(profile));

        when(reviewRepository.getAverageRatingsForExperts()).thenReturn(List.of());
        when(projectRepository.countTotalProjectsGroupByExpert()).thenReturn(List.of());
        when(projectRepository.countCompletedProjectsGroupByExpert()).thenReturn(List.of());
        when(portfolioRepository.getPortfolioCountsForExperts()).thenReturn(List.of());

        when(recommendationRepository.findByJobIdAndExpertId(5L, 2L)).thenReturn(Optional.empty());
        when(recommendationRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        List<RecommendationResponseDTO> results = recommendationService.recommendExpertsForJob(5L);

        assertThat(results).hasSize(1);
        RecommendationResponseDTO dto = results.get(0);
        assertThat(dto.getRatingScore()).isEqualTo(80.0);
        assertThat(dto.getSuccessRateScore()).isEqualTo(100.0);
        assertThat(dto.getPortfolioScore()).isEqualTo(0.0);
    }
}
