package com.aitasker.recommendation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationAnalyticsResponse {
    private long totalRecommendations;
    private long totalAcceptedRecommendations;
    private double acceptanceRate;              // Tỷ lệ chấp nhận gợi ý (%)
    private double proposalConversionRate;       // Tỷ lệ chuyển đổi proposal (%)
    private double recommendationAccuracy;        // Độ chính xác hệ thống (%)
    private double averageMatchScoreOfAccepted;   // Điểm khớp trung bình của các gợi ý được nhận
    private double averageMatchScoreOverall;      // Điểm khớp trung bình chung
}
