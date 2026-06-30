package com.aitasker.ai.assistant.impl;

import com.aitasker.ai.assistant.ServiceGeneratorService;
import com.aitasker.ai.dto.response.ServiceGeneratorResponse;
import com.aitasker.ai.openai.OpenAiClient;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;

@Service
public class ServiceGeneratorServiceImpl implements ServiceGeneratorService {

    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ServiceGeneratorServiceImpl(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    @Override
    public ServiceGeneratorResponse generateServicePackage(String userPrompt) {
        String systemPrompt = "You are an AI Service Generator. Help an expert create a service marketplace listing in Vietnamese based on their skill brief. "
                + "You MUST respond with a valid JSON object containing exactly these fields: "
                + "'serviceDescription' (String), 'tags' (Array of Strings), 'pricingSuggestion' (String).";

        try {
            String aiResponse = openAiClient.generate(systemPrompt, userPrompt);
            return objectMapper.readValue(aiResponse, ServiceGeneratorResponse.class);
        } catch (Exception e) {
            return ServiceGeneratorResponse.builder()
                    .serviceDescription("Mô tả dịch vụ chuyên gia: " + userPrompt)
                    .tags(Arrays.asList("Expert", "Consulting"))
                    .pricingSuggestion("Liên hệ báo giá")
                    .build();
        }
    }
}