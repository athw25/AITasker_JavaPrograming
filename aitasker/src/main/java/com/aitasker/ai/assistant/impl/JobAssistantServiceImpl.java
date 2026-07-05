package com.aitasker.ai.assistant.impl;

import com.aitasker.ai.assistant.JobAssistantService;
import com.aitasker.ai.dto.response.JobAssistantResponse;
import com.aitasker.ai.openai.OpenAiClient;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;

@Service
public class JobAssistantServiceImpl implements JobAssistantService {

    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JobAssistantServiceImpl(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    @Override
    public JobAssistantResponse generateJobDescription(String userPrompt) {
        String systemPrompt = "You are an AI Job Assistant. Generate a professional job posting in Vietnamese based on user's brief request. "
                + "You MUST respond with a valid JSON object containing exactly these fields: "
                + "'title' (String), 'description' (String), 'skills' (Array of Strings), 'budgetSuggestion' (String).";

        try {
            String aiResponse = openAiClient.generate(systemPrompt, userPrompt);
            return objectMapper.readValue(aiResponse, JobAssistantResponse.class);
        } catch (Exception e) {
            return JobAssistantResponse.builder()
                    .title("Yêu cầu tuyển dụng chuyên gia AI")
                    .description("Chi tiết công việc: " + userPrompt)
                    .skills(Arrays.asList("Java", "Spring Boot"))
                    .budgetSuggestion("Thỏa thuận")
                    .build();
        }
    }
}