package com.aitasker.ai.assistant.impl;

import com.aitasker.ai.assistant.ServiceGeneratorService;
import com.aitasker.ai.dto.response.ServiceGeneratorResponse;
import com.aitasker.ai.gateway.AiGateway;
import com.aitasker.ai.prompt.PromptBuilder;
import com.aitasker.ai.util.AiJsonExtractor;
import com.aitasker.exception.AiUnavailableException;
import com.aitasker.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceGeneratorServiceImpl implements ServiceGeneratorService {

    private final AiGateway aiGateway;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    @Override
    public ServiceGeneratorResponse generateServicePackage(String userPrompt) {
        if (!aiGateway.isAvailable()) {
            throw new AiUnavailableException(
                    "AI Service Generator hiện không khả dụng. Vui lòng bật AI Provider trong cấu hình hệ thống.");
        }

        String prompt = promptBuilder.buildServiceGeneratorPrompt(userPrompt);
        String rawResponse;
        try {
            rawResponse = aiGateway.call(prompt);
        } catch (Exception e) {
            log.error("Gọi AI Provider thất bại (Service Generator): {}", e.getMessage());
            throw new BusinessException("Không thể kết nối tới AI Provider, vui lòng thử lại sau.");
        }

        try {
            String json = AiJsonExtractor.extractJsonObject(rawResponse);
            return objectMapper.readValue(json, ServiceGeneratorResponse.class);
        } catch (Exception e) {
            log.error("Không thể phân tích phản hồi AI Service Generator: {}", e.getMessage());
            throw new BusinessException("AI trả về dữ liệu không hợp lệ, vui lòng thử lại.");
        }
    }
}
