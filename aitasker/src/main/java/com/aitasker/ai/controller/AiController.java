package com.aitasker.ai.controller;

import com.aitasker.ai.assistant.JobAssistantService;
import com.aitasker.ai.assistant.ServiceGeneratorService;
import com.aitasker.ai.dto.request.JobAssistantRequest;
import com.aitasker.ai.dto.request.ServiceGeneratorRequest;
import com.aitasker.ai.dto.response.ExpertRecommendationResponse;
import com.aitasker.ai.dto.response.JobAssistantResponse;
import com.aitasker.ai.dto.response.ServiceGeneratorResponse;
import com.aitasker.ai.recommendation.ExpertRecommendationService;
import com.aitasker.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Modules", description = "AI Job Assistant, Service Generator, Expert Recommendation")
public class AiController {

    private final JobAssistantService jobAssistantService;
    private final ServiceGeneratorService serviceGeneratorService;
    private final ExpertRecommendationService expertRecommendationService;

    @PostMapping("/job-assistant")
    public ApiResponse<JobAssistantResponse> jobAssistant(@Valid @RequestBody JobAssistantRequest request) {
        return ApiResponse.success(jobAssistantService.generate(request));
    }

    @PostMapping("/service-generator")
    public ApiResponse<ServiceGeneratorResponse> serviceGenerator(@Valid @RequestBody ServiceGeneratorRequest request) {
        return ApiResponse.success(serviceGeneratorService.generate(request));
    }

    @GetMapping("/recommend-experts/{jobId}")
    public ApiResponse<List<ExpertRecommendationResponse>> recommendExperts(@PathVariable Long jobId) {
        return ApiResponse.success(expertRecommendationService.recommend(jobId));
    }
}
