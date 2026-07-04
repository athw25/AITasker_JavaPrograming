package com.aitasker.admin.service;

import com.aitasker.admin.analytics.AnalyticsStatistics;
import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.common.enums.ProposalStatus;
import com.aitasker.common.enums.Role;
import com.aitasker.payment.enums.PaymentStatus;
import com.aitasker.payment.repository.PaymentRepository;
import com.aitasker.proposal.repository.ProposalRepository;
import com.aitasker.review.repository.ReviewRepository;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService{
    private final UserRepository userRepository;
    private final ProposalRepository proposalRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    @Override
    public AnalyticsStatistics getAnalytics() {
        return AnalyticsStatistics.builder()
                .totalExperts(userRepository.countByRole(Role.EXPERT))
                .totalClients(userRepository.countByRole(Role.CLIENT))

                .totalProposals(proposalRepository.count())
                .acceptedProposals(proposalRepository.countByStatus(ProposalStatus.ACCEPTED))
                .rejectedProposals(proposalRepository.countByStatus(ProposalStatus.REJECTED))

                .totalRevenue(paymentRepository.getTotalRevenue())
                .pendingPayments(paymentRepository.countByStatus(PaymentStatus.PENDING))
                .heldPayments(paymentRepository.countByStatus(PaymentStatus.HELD))
                .releasedPayments(paymentRepository.countByStatus(PaymentStatus.RELEASED))

                .averageRating(reviewRepository.getAverageRating())

                .build();
    }
}
