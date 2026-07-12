package com.aitasker.ai.assistant.impl;

import com.aitasker.ai.assistant.ServiceGeneratorService;
import com.aitasker.ai.dto.response.ServiceGeneratorResponse;
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
public class ServiceGeneratorServiceImpl implements ServiceGeneratorService {

    private final AiGateway aiGateway;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    @Override
    public ServiceGeneratorResponse generateServicePackage(String userPrompt) {
        String prompt = promptBuilder.buildServiceGeneratorPrompt(userPrompt);
        String aiResponse = aiGateway.call(prompt);

        if (aiResponse == null || aiResponse.isBlank()) {
            return fallbackResponse(userPrompt);
        }

        try {
            String json = AiJsonParser.extractJson(aiResponse);
            return objectMapper.readValue(json, ServiceGeneratorResponse.class);
        } catch (Exception e) {
            log.warn("Không parse được phản hồi AI Service Generator, dùng dữ liệu mặc định: {}", e.getMessage());
            return fallbackResponse(userPrompt);
        }
    }

    private ServiceGeneratorResponse fallbackResponse(String userPrompt) {
        return ServiceGeneratorResponse.builder()
                .serviceDescription("Mô tả dịch vụ chuyên gia: " + userPrompt)
                .tags(Arrays.asList("Expert", "Consulting"))
                .pricingSuggestion("Liên hệ báo giá")
                .build();
    }
}
