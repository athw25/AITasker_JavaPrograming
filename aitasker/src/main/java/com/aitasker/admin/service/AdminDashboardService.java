package com.aitasker.admin.service;

import com.aitasker.admin.dashboard.DashboardStatistics;
import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.common.enums.ProposalStatus;
import com.aitasker.common.enums.Role;
import com.aitasker.dispute.repository.DisputeRepository;
import com.aitasker.common.enums.DisputeStatus;
import com.aitasker.job.repository.JobPostRepository;
import com.aitasker.payment.enums.TransactionType;
import com.aitasker.payment.repository.TransactionRepository;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.proposal.repository.ProposalRepository;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final JobPostRepository jobPostRepository;
    private final ProposalRepository proposalRepository;
    private final ProjectRepository projectRepository;
    private final TransactionRepository transactionRepository;
    private final DisputeRepository disputeRepository;

    public DashboardStatistics getStatistics() {
        long totalUsers = userRepository.count();
        long totalClients = userRepository.findAll().stream().filter(u -> u.getRole() == Role.CLIENT).count();
        long totalExperts = userRepository.findAll().stream().filter(u -> u.getRole() == Role.EXPERT).count();

        long totalJobs = jobPostRepository.count();
        long totalProposals = proposalRepository.count();
        long acceptedProposals = proposalRepository.findByStatus(ProposalStatus.ACCEPTED).size();

        long totalProjects = projectRepository.count();
        long completedProjects = projectRepository.findByStatus(ProjectStatus.COMPLETED).size();

        long totalDisputes = disputeRepository.count();
        long openDisputes = disputeRepository.findByStatus(DisputeStatus.OPEN).size();

        double completionRate = totalProjects == 0 ? 0 : (double) completedProjects / totalProjects * 100;
        double acceptanceRate = totalProposals == 0 ? 0 : (double) acceptedProposals / totalProposals * 100;

        return new DashboardStatistics(
                totalUsers,
                totalClients,
                totalExperts,
                totalJobs,
                totalProposals,
                totalProjects,
                completedProjects,
                Math.round(completionRate * 100.0) / 100.0,
                Math.round(acceptanceRate * 100.0) / 100.0,
                transactionRepository.sumAmountByType(TransactionType.RELEASE),
                totalDisputes,
                openDisputes
        );
    }
}
