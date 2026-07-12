package com.aitasker.ai.provider;

import com.aitasker.ai.config.AiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenAiProvider implements AiProvider {

    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;
    private static final String PROVIDER_NAME = "openai";

    @Override
    public String getProviderName() { return PROVIDER_NAME; }

    @Override
    public boolean isAvailable() {
        AiProperties.OpenAi cfg = aiProperties.getOpenai();
        return aiProperties.isEnabled()
                && PROVIDER_NAME.equals(aiProperties.getProvider())
                && cfg != null
                && !cfg.getApiKey().isBlank()
                && !cfg.getApiKey().startsWith("YOUR_");
    }

    @Override
    public String call(String prompt) {
        AiProperties.OpenAi cfg = aiProperties.getOpenai();
        try {
            String body = objectMapper.writeValueAsString(Map.of(
                    "model", cfg.getModel(),
                    "messages", List.of(Map.of("role", "user", "content", prompt)),
                    "temperature", 0.7));
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(cfg.getTimeoutSeconds())).build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(cfg.getBaseUrl() + "/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + cfg.getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode node = objectMapper.readTree(response.body());
            return node.path("choices").path(0).path("message").path("content").asText("");
        } catch (Exception e) {
            log.error("OpenAI call failed: {}", e.getMessage());
            return "";
        }
    }
}
