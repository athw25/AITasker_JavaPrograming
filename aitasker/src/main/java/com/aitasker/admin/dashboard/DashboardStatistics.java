package com.aitasker.admin.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatistics {
    private long totalUsers;
    private long totalClients;
    private long totalExperts;
    private long totalJobs;
    private long totalProposals;
    private long totalProjects;
    private long completedProjects;
    private double projectCompletionRate;
    private double proposalAcceptanceRate;
    private BigDecimal totalRevenue;
    private long totalDisputes;
    private long openDisputes;
}
