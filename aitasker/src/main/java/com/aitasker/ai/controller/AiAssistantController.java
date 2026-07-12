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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
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
    public ApiResponse<JobAssistantResponse> assistJob(@RequestParam String prompt,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        JobAssistantResponse response = jobAssistantService.generateJobDescription(prompt);
        recordAiPromptUsed(userDetails, "JobAssistant");
        return ApiResponse.success("AI sinh thông tin công việc thành công!", response);
    }

    @PostMapping("/service-generator")
    @Operation(summary = "Tự động thiết kế gói bài viết dịch vụ, thẻ tags và gợi ý giá bán cho Chuyên gia bằng AI")
    public ApiResponse<ServiceGeneratorResponse> generateService(@RequestParam String prompt,
                                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        ServiceGeneratorResponse response = serviceGeneratorService.generateServicePackage(prompt);
        recordAiPromptUsed(userDetails, "ServiceGenerator");
        return ApiResponse.success("AI sinh gói dịch vụ chuyên gia thành công!", response);
    }

    private void recordAiPromptUsed(CustomUserDetails userDetails, String module) {
        if (userDetails == null) {
            return;
        }
        analyticsService.recordEvent(AnalyticsEventType.AI_PROMPT_USED, userDetails.getUser().getId(),
                userDetails.getUser().getRole().name(), module, null);
    }
}
