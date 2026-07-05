package com.aitasker.admin.controller;

import com.aitasker.admin.dashboard.DashboardStatistics;
import com.aitasker.admin.service.AdminStatisticsService;
import com.aitasker.admin.service.AdminUserService;
import com.aitasker.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Statistics")
public class AdminStatisticsController {
    private final AdminStatisticsService adminStatisticsService;
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get dashboard statistics")
    public ApiResponse<DashboardStatistics>getDashboard(){
        return ApiResponse.success(
                adminStatisticsService.getDashboardStatistics());
    }
}
