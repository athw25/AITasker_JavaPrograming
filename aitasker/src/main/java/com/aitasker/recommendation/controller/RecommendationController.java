package com.aitasker.recommendation.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.recommendation.dto.response.RecommendationResponseDTO;
import com.aitasker.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// Tạm thời bỏ RequestMapping chung để định nghĩa đường dẫn riêng cho từng API
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    // API 1: Khớp chuẩn với yêu cầu GET /api/ai/recommend-experts/{jobId}
    @GetMapping("/api/ai/recommend-experts/{jobId}")
    public ApiResponse<List<RecommendationResponseDTO>> getExpertRecommendations(@PathVariable Long jobId) {
        List<RecommendationResponseDTO> recommendations = recommendationService.recommendExpertsForJob(jobId);
        return ApiResponse.success("Lấy danh sách đề xuất chuyên gia thành công", recommendations);
    }

    // API 2: Khớp chuẩn với yêu cầu GET /api/recommendations
    @GetMapping("/api/recommendations")
    public ApiResponse<List<RecommendationResponseDTO>> getAllRecommendationHistory() {
        // Bạn sẽ cần thêm một hàm getAllRecommendations() ở Service để gọi repository.findAll()
        List<RecommendationResponseDTO> history = recommendationService.getAllRecommendations();
        return ApiResponse.success("Lấy toàn bộ lịch sử đánh giá đề xuất thành công", history);
    }
}