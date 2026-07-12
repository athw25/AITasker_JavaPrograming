package com.aitasker.ai.controller;

import com.aitasker.analytics.enums.AnalyticsEventType;
import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.common.response.ApiResponse;
import com.aitasker.ai.assistant.JobAssistantService;
import com.aitasker.ai.assistant.ServiceGeneratorService;
import com.aitasker.ai.dto.response.JobAssistantResponse;
import com.aitasker.ai.dto.response.ServiceGeneratorResponse;
import com.aitasker.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@Validated
@Tag(name = "AI Assistant Module", description = "Các tính năng tự động hóa và hỗ trợ hệ sinh thái bằng Generative AI")
public class AiAssistantController {

    private final JobAssistantService jobAssistantService;
    private final ServiceGeneratorService serviceGeneratorService;
    private final AnalyticsService analyticsService;

    public AiAssistantController(JobAssistantService jobAssistantService,
                                  ServiceGeneratorService serviceGeneratorService,
                                  AnalyticsService analyticsService) {
        this.jobAssistantService = jobAssistantService;
        this.serviceGeneratorService = serviceGeneratorService;
        this.analyticsService = analyticsService;
    }

    @PostMapping("/job-assistant")
    @Operation(summary = "Tự động sinh tiêu đề, mô tả, kỹ năng và ngân sách công việc cho khách hàng bằng AI")
    public ApiResponse<JobAssistantResponse> assistJob(
            @RequestParam @NotBlank(message = "Prompt không được để trống") String prompt,
            @AuthenticationPrincipal CustomUserDetails principal) {
        JobAssistantResponse response = jobAssistantService.generateJobDescription(prompt);
        recordAiPromptUsed(principal, "JOB_ASSISTANT");
        return ApiResponse.success("AI sinh thông tin công việc thành công!", response);
    }

    @PostMapping("/service-generator")
    @Operation(summary = "Tự động thiết kế gói bài viết dịch vụ, thẻ tags và gợi ý giá bán cho Chuyên gia bằng AI")
    public ApiResponse<ServiceGeneratorResponse> generateService(
            @RequestParam @NotBlank(message = "Prompt không được để trống") String prompt,
            @AuthenticationPrincipal CustomUserDetails principal) {
        ServiceGeneratorResponse response = serviceGeneratorService.generateServicePackage(prompt);
        recordAiPromptUsed(principal, "SERVICE_GENERATOR");
        return ApiResponse.success("AI sinh gói dịch vụ chuyên gia thành công!", response);
    }

    private void recordAiPromptUsed(CustomUserDetails principal, String module) {
        Long actorId = principal != null ? principal.getUser().getId() : null;
        String actorRole = principal != null ? principal.getUser().getRole().name() : null;
        analyticsService.recordEvent(AnalyticsEventType.AI_PROMPT_USED, actorId, actorRole, "AI_MODULE", module);
    }
}