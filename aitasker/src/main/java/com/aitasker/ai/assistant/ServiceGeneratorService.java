package com.aitasker.ai.assistant;

import com.aitasker.ai.dto.request.ServiceGeneratorRequest;
import com.aitasker.ai.dto.response.ServiceGeneratorResponse;
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
public class ServiceGeneratorService {

    private static final String SYSTEM_PROMPT =
            "Bạn là trợ lý tạo mô tả gói dịch vụ AI cho marketplace. " +
            "Trả lời DUY NHẤT một JSON object với các trường: " +
            "title (string), description (string), tags (array of string), price (number, đơn vị USD). " +
            "Không thêm text nào khác ngoài JSON.";

    private final OpenAiClient openAiClient;
    private final AnalyticsService analyticsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ServiceGeneratorResponse generate(ServiceGeneratorRequest request) {
        analyticsService.record("AI_SERVICE_GENERATOR_USED", null, request.getPrompt());
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

    private ServiceGeneratorResponse parse(String raw) {
        try {
            String json = extractJson(raw);
            JsonNode node = objectMapper.readTree(json);
            List<String> tags = new ArrayList<>();
            node.path("tags").forEach(t -> tags.add(t.asText()));
            return new ServiceGeneratorResponse(
                    node.path("title").asText(),
                    node.path("description").asText(),
                    tags,
                    BigDecimal.valueOf(node.path("price").asDouble(0))
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

    private ServiceGeneratorResponse heuristic(String prompt) {
        String title = capitalize(prompt.strip());
        if (title.length() > 80) title = title.substring(0, 80);

        String description = "Professional AI service: " + prompt.strip() + ".";
        List<String> tags = new ArrayList<>(Arrays.asList("AI", "Automation"));

        return new ServiceGeneratorResponse(title, description, tags, BigDecimal.valueOf(200));
    }

    private String capitalize(String text) {
        if (text.isEmpty()) return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
