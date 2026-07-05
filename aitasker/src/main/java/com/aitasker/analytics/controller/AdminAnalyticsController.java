package com.aitasker.analytics.controller;

import com.aitasker.analytics.dto.AnalyticsResponse;
import com.aitasker.analytics.dto.DashboardSummaryResponse;
import com.aitasker.analytics.dto.ReportResponse;
import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Analytics", description = "Dashboard, analytics and reports for admins")
public class AdminAnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/reports")
    @Operation(summary = "Get monthly trend reports")
    public ApiResponse<ReportResponse> getReports() {
        return ApiResponse.success("Reports loaded", analyticsService.getReports());
    }
}