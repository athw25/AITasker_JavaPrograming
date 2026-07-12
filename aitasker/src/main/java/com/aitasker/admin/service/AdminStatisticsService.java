package com.aitasker.admin.service;

import com.aitasker.admin.dashboard.DashboardStatistics;
import com.aitasker.common.enums.JobStatus;
import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.common.enums.Role;
import com.aitasker.job.repository.JobPostRepository;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.review.repository.ReviewRepository;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminStatisticsService {

    private final UserRepository userRepository;
    private final JobPostRepository jobPostRepository;
    private final ProjectRepository projectRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public DashboardStatistics getDashboardStatistics() {
        return DashboardStatistics.builder()
                .totalUsers(userRepository.count())
                .totalClients(userRepository.countByRole(Role.CLIENT))
                .totalExperts(userRepository.countByRole(Role.EXPERT))
                .totalJobs(jobPostRepository.count())
                .openJobs(jobPostRepository.countByStatus(JobStatus.OPEN))
                .completedJobs(jobPostRepository.countByStatus(JobStatus.CLOSED))
                .totalProjects(projectRepository.count())
                .completedProjects(projectRepository.findAll().stream()
                        .filter(project -> project.getStatus() == ProjectStatus.COMPLETED)
                        .count())
                .totalReviews(reviewRepository.count())
                .build();
    }
}
