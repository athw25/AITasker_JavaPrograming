package com.aitasker.recommendation.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.recommendation.dto.response.RecommendationResponseDTO;
import com.aitasker.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    // API 1: GET /api/ai/recommend-experts/{jobId}
    @GetMapping("/api/ai/recommend-experts/{jobId}")
    public ApiResponse<List<RecommendationResponseDTO>> getExpertRecommendations(@PathVariable Long jobId) {
        List<RecommendationResponseDTO> recommendations = recommendationService.recommendExpertsForJob(jobId);
        return ApiResponse.success("Lấy danh sách đề xuất chuyên gia thành công", recommendations);
    }

    // API 2: GET /api/recommendations
    @GetMapping("/api/recommendations")
    public ApiResponse<List<RecommendationResponseDTO>> getAllRecommendationHistory() {
        // Bạn sẽ cần thêm một hàm getAllRecommendations() ở Service để gọi repository.findAll()
        List<RecommendationResponseDTO> history = recommendationService.getAllRecommendations();
        return ApiResponse.success("Lấy toàn bộ lịch sử đánh giá đề xuất thành công", history);
    }
    // API Bổ sung: Xem lịch sử Feedback của 1 Job cụ thể
    @GetMapping("/api/recommendations/history/{jobId}")
    public ApiResponse<List<RecommendationResponseDTO>> getFeedbackHistoryByJob(@PathVariable Long jobId) {
        List<RecommendationResponseDTO> history = recommendationService.getRecommendationHistory(jobId);
        return ApiResponse.success("Lấy lịch sử feedback của công việc thành công", history);
    }
}