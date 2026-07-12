package com.aitasker.ai.assistant.impl;

import com.aitasker.ai.assistant.JobAssistantService;
import com.aitasker.ai.dto.response.JobAssistantResponse;
import com.aitasker.ai.gateway.AiGateway;
import com.aitasker.ai.prompt.PromptBuilder;
import com.aitasker.ai.util.AiJsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobAssistantServiceImpl implements JobAssistantService {

    private final AiGateway aiGateway;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    @Override
    public JobAssistantResponse generateJobDescription(String userPrompt) {
        String prompt = promptBuilder.buildJobAssistantPrompt(userPrompt);
        String aiResponse = aiGateway.call(prompt);

        if (aiResponse == null || aiResponse.isBlank()) {
            return fallbackResponse(userPrompt);
        }

        try {
            String json = AiJsonParser.extractJson(aiResponse);
            return objectMapper.readValue(json, JobAssistantResponse.class);
        } catch (Exception e) {
            log.warn("Không parse được phản hồi AI Job Assistant, dùng dữ liệu mặc định: {}", e.getMessage());
            return fallbackResponse(userPrompt);
        }
    }

    private JobAssistantResponse fallbackResponse(String userPrompt) {
        return JobAssistantResponse.builder()
                .title("Yêu cầu tuyển dụng chuyên gia AI")
                .description("Chi tiết công việc: " + userPrompt)
                .skills(Arrays.asList("Java", "Spring Boot"))
                .budgetSuggestion("Thỏa thuận")
                .build();
    }
}
