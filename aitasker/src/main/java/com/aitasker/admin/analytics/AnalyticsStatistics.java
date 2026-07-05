package com.aitasker.admin.analytics;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsStatistics {
    //Payment
    private BigDecimal totalRevenue;
    private Long heldPayments;
    private Long releasedPayments;
    private Long pendingPayments;

    //Proposal
    private Long totalProposals;
    private Long acceptedProposals;
    private Long rejectedProposals;

    //Marketplace
    private Long totalExperts;
    private Long totalClients;

    //Review
    private Double averageRating;
}
