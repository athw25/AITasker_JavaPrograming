package com.aitasker.recommendation.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.common.response.PageResponse;
import com.aitasker.recommendation.dto.response.RecommendationAnalyticsResponse;
import com.aitasker.recommendation.dto.response.RecommendationResponseDTO;
import com.aitasker.recommendation.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.aitasker.recommendation.dto.request.RecommendationFeedbackRequest;
import com.aitasker.recommendation.dto.response.RecommendationFeedbackResponse;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Recommendation", description = "API Đề xuất chuyên gia AI & Thống kê RQ1")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(summary = "Tính toán và đưa ra danh sách đề xuất Chuyên gia AI", description = "Chỉ cho phép Client tạo Job hoặc Admin thực hiện.")
    @GetMapping("/ai/recommend-experts/{jobId}")
    public ApiResponse<List<RecommendationResponseDTO>> getExpertRecommendations(@PathVariable Long jobId) {
        List<RecommendationResponseDTO> recommendations = recommendationService.recommendExpertsForJob(jobId);
        return ApiResponse.success("Lấy danh sách đề xuất chuyên gia thành công", recommendations);
    }

    @Operation(summary = "Xem lịch sử đề xuất của một Công việc", description = "Chỉ cho phép Client tạo Job hoặc Admin xem lịch sử.")
    @GetMapping("/recommendations/history/{jobId}")
    public ApiResponse<List<RecommendationResponseDTO>> getFeedbackHistoryByJob(@PathVariable Long jobId) {
        List<RecommendationResponseDTO> history = recommendationService.getRecommendationHistory(jobId);
        return ApiResponse.success("Lấy lịch sử feedback của công việc thành công", history);
    }

    @Operation(summary = "Lấy danh sách toàn bộ lịch sử đề xuất (Phân trang)", description = "Quyền truy cập: ADMIN")
    @GetMapping("/admin/recommendations")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<RecommendationResponseDTO>> getAllRecommendationHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PageResponse<RecommendationResponseDTO> history = recommendationService.getAllRecommendations(page, size, sortBy, sortDir);
        return ApiResponse.success("Lấy toàn bộ lịch sử đánh giá đề xuất thành công", history);
    }

    @Operation(summary = "Lấy báo cáo Analytics hiệu năng phục vụ nghiên cứu RQ1", description = "Quyền truy cập: ADMIN")
    @GetMapping("/admin/recommendations/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<RecommendationAnalyticsResponse> getRecommendationAnalytics() {
        RecommendationAnalyticsResponse analytics = recommendationService.getAnalytics();
        return ApiResponse.success("Lấy thông tin Analytics thành công", analytics);
    }

    @Operation(summary = "Lưu feedback đề xuất chuyên gia (TV4 Bổ sung)")
    @PostMapping("/recommendations/feedback")
    public ApiResponse<RecommendationFeedbackResponse> saveFeedback(@Valid @RequestBody RecommendationFeedbackRequest request) {
        RecommendationFeedbackResponse response = recommendationService.saveFeedback(request);
        return ApiResponse.success("Lưu feedback đề xuất thành công", response);
    }

    @Operation(summary = "Lấy lịch sử feedback đề xuất của một công việc (TV4 Bổ sung)")
    @GetMapping("/recommendations/feedback/history/{jobId}")
    public ApiResponse<List<RecommendationFeedbackResponse>> getFeedbackHistoryForJob(@PathVariable Long jobId) {
        List<RecommendationFeedbackResponse> history = recommendationService.getFeedbackHistoryForJob(jobId);
        return ApiResponse.success("Lấy lịch sử feedback đề xuất thành công", history);
    }
}
