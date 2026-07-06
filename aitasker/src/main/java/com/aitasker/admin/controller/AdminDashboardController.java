package com.aitasker.admin.controller;

import com.aitasker.admin.dashboard.DashboardStatistics;
import com.aitasker.admin.service.AdminDashboardService;
import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private static final List<String> EVENT_TYPES = List.of(
            "PROPOSAL_CREATED", "PROPOSAL_ACCEPTED",
            "AI_JOB_ASSISTANT_USED", "AI_SERVICE_GENERATOR_USED"
    );

    private final AdminDashboardService adminDashboardService;
    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ApiResponse<DashboardStatistics> dashboard() {
        return ApiResponse.success(adminDashboardService.getStatistics());
    }

    @GetMapping("/analytics")
    public ApiResponse<Map<String, Long>> analytics() {
        return ApiResponse.success(analyticsService.countByEventTypes(EVENT_TYPES));
    }

    @GetMapping("/reports")
    public ApiResponse<DashboardStatistics> reports() {
        return ApiResponse.success(adminDashboardService.getStatistics());
    }
}
