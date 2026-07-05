package com.aitasker.ai.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.ai.assistant.JobAssistantService;
import com.aitasker.ai.assistant.ServiceGeneratorService;
import com.aitasker.ai.dto.response.JobAssistantResponse;
import com.aitasker.ai.dto.response.ServiceGeneratorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Assistant Module", description = "Các tính năng tự động hóa và hỗ trợ hệ sinh thái bằng Generative AI")
public class AiAssistantController {

    private final JobAssistantService jobAssistantService;
    private final ServiceGeneratorService serviceGeneratorService;

    public AiAssistantController(JobAssistantService jobAssistantService, ServiceGeneratorService serviceGeneratorService) {
        this.jobAssistantService = jobAssistantService;
        this.serviceGeneratorService = serviceGeneratorService;
    }

    @PostMapping("/job-assistant")
    @Operation(summary = "Tự động sinh tiêu đề, mô tả, kỹ năng và ngân sách công việc cho khách hàng bằng AI")
    public ApiResponse<JobAssistantResponse> assistJob(@RequestParam String prompt) {
        JobAssistantResponse response = jobAssistantService.generateJobDescription(prompt);
        return ApiResponse.success("AI sinh thông tin công việc thành công!", response);
    }

    @PostMapping("/service-generator")
    @Operation(summary = "Tự động thiết kế gói bài viết dịch vụ, thẻ tags và gợi ý giá bán cho Chuyên gia bằng AI")
    public ApiResponse<ServiceGeneratorResponse> generateService(@RequestParam String prompt) {
        ServiceGeneratorResponse response = serviceGeneratorService.generateServicePackage(prompt);
        return ApiResponse.success("AI sinh gói dịch vụ chuyên gia thành công!", response);
    }
}