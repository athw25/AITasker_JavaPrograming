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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService{
    private final UserRepository userRepository;
    private final ProposalRepository proposalRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    @Override
    @Transactional(readOnly = true)
    public AnalyticsStatistics getAnalytics() {
        return AnalyticsStatistics.builder()
                .totalExperts(userRepository.countByRole(Role.EXPERT))
                .totalClients(userRepository.countByRole(Role.CLIENT))

                .totalProposals(proposalRepository.count())
                .acceptedProposals(proposalRepository.countByStatus(ProposalStatus.ACCEPTED))
                .rejectedProposals(proposalRepository.countByStatus(ProposalStatus.REJECTED))

                .totalRevenue(
                        Optional.ofNullable(paymentRepository.getTotalRevenue())
                                .orElse(BigDecimal.ZERO)
                )
                .pendingPayments(paymentRepository.countByStatus(PaymentStatus.PENDING))
                .heldPayments(paymentRepository.countByStatus(PaymentStatus.HELD))
                .releasedPayments(paymentRepository.countByStatus(PaymentStatus.RELEASED))

                .averageRating(
                        Optional.ofNullable(reviewRepository.getAverageRating())
                                .orElse(0.0)
                )

                .build();
    }
}
