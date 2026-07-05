package com.aitasker.admin.dashboard;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatistics{
    //User stats
    private long totalUsers;
    private long totalClients;
    private long totalExperts;
    //Job stats
    private long totalJobs;
    private long openJobs;
    private long completedJobs;
    // Project stats
    private long totalProjects;
    private long completedProjects;
    //Review stats
    private long totalReviews;
}
