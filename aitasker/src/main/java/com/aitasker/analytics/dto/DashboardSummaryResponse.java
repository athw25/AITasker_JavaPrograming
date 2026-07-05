package com.aitasker.analytics.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class DashboardSummaryResponse {

    private long totalUsers;
    private long totalClients;
    private long totalExperts;

    private long totalJobs;
    private long activeJobs;

    private long totalProposals;
    private long totalProjects;
    private long activeProjects;
    private long completedProjects;

    private double projectSuccessRate;
    private double proposalAcceptanceRate;

    private BigDecimal totalRevenue;
    private BigDecimal revenueThisMonth;

    private long aiPromptsUsed;
    private long recommendationsGenerated;

    private String activeAiProvider;
    private boolean aiEnabled;
}