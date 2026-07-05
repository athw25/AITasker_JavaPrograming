package com.aitasker.admin.controller;

import com.aitasker.admin.analytics.AnalyticsStatistics;
import com.aitasker.admin.service.AnalyticsService;
import com.aitasker.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AnalyticsStatistics> getAnalytics(){
        return ApiResponse.success(
                analyticsService.getAnalytics());
    }
}
