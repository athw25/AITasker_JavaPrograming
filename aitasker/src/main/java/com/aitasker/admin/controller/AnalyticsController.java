package com.aitasker.admin.controller;

import com.aitasker.admin.analytics.AnalyticsStatistics;
import com.aitasker.admin.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<AnalyticsStatistics> getAnalytics(){
        return ResponseEntity.ok(analyticsService.getAnalytics());
    }
}
