package com.aitasker.ai.assistant;

import com.aitasker.ai.dto.request.JobAssistantRequest;
import com.aitasker.ai.dto.response.JobAssistantResponse;
import com.aitasker.ai.openai.OpenAiClient;
import com.aitasker.analytics.service.AnalyticsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobAssistantService {

    private static final String SYSTEM_PROMPT =
            "Bạn là trợ lý tạo Job Post cho marketplace AI. " +
            "Trả lời DUY NHẤT một JSON object với các trường: " +
            "title (string), description (string), skills (array of string), budget (number, đơn vị USD). " +
            "Không thêm text nào khác ngoài JSON.";

    private final OpenAiClient openAiClient;
    private final AnalyticsService analyticsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JobAssistantResponse generate(JobAssistantRequest request) {
        analyticsService.record("AI_JOB_ASSISTANT_USED", null, request.getPrompt());
        if (openAiClient.isAvailable()) {
            try {
                String raw = openAiClient.chat(SYSTEM_PROMPT, request.getPrompt());
                return parse(raw);
            } catch (Exception e) {
                log.warn("OpenAI thất bại, fallback sang heuristic: {}", e.getMessage());
            }
        }
        return heuristic(request.getPrompt());
    }

    private JobAssistantResponse parse(String raw) {
        try {
            String json = extractJson(raw);
            JsonNode node = objectMapper.readTree(json);
            List<String> skills = new ArrayList<>();
            node.path("skills").forEach(s -> skills.add(s.asText()));
            return new JobAssistantResponse(
                    node.path("title").asText(),
                    node.path("description").asText(),
                    skills,
                    BigDecimal.valueOf(node.path("budget").asDouble(0))
            );
        } catch (Exception e) {
            log.warn("Không parse được JSON từ AI, fallback heuristic", e);
            return heuristic(raw);
        }
    }

    private String extractJson(String raw) {
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return raw.substring(start, end + 1);
        }
        return raw;
    }

    private JobAssistantResponse heuristic(String prompt) {
        String title = capitalize(prompt.strip());
        if (title.length() > 80) title = title.substring(0, 80);

        String description = "Develop an AI solution based on the request: \"" + prompt.strip() + "\".";

        List<String> skills = new ArrayList<>(Arrays.asList("Python", "OpenAI API"));
        String lower = prompt.toLowerCase();
        if (lower.contains("chatbot") || lower.contains("facebook") || lower.contains("messenger")) {
            skills.add("LangChain");
        }
        if (lower.contains("resume") || lower.contains("parser") || lower.contains("nlp")) {
            skills.add("NLP");
        }
        if (lower.contains("image") || lower.contains("vision")) {
            skills.add("Computer Vision");
        }

        return new JobAssistantResponse(title, description, skills, BigDecimal.valueOf(3000));
    }

    private String capitalize(String text) {
        if (text.isEmpty()) return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
