package com.aitasker.analytics.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReportResponse {
    private List<MonthlyDataPoint> monthlyRevenue;
    private List<MonthlyDataPoint> monthlyNewUsers;
    private List<MonthlyDataPoint> monthlyNewJobs;
    private List<MonthlyDataPoint> monthlyCompletedProjects;
    private List<MonthlyDataPoint> monthlyAiPrompts;
}