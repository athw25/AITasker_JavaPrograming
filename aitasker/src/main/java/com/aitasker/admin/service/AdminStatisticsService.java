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
    public DashboardStatistics getDashboardStatistics(){
        return DashboardStatistics.builder()
                // User stats
                .totalUsers(userRepository.count())
                .totalClients(userRepository.countByRole(Role.CLIENT))
                .totalExperts(userRepository.countByRole(Role.EXPERT))
                //Job stats
                .totalJobs(jobPostRepository.count())
                .openJobs(jobPostRepository.countByStatus(JobStatus.OPEN))
                .completedJobs(jobPostRepository.countByStatus(JobStatus.CLOSED))
                //Project stats
                .totalProjects(projectRepository.count())
                .completedProjects(projectRepository.countByStatus(ProjectStatus.COMPLETED))
                //Review stats
                .totalReviews(reviewRepository.count())
                .build();

    }
}
