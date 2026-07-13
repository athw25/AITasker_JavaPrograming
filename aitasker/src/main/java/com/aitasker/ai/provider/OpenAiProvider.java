package com.aitasker.ai.provider;

import com.aitasker.ai.config.AiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenAiProvider implements AiProvider {

    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;

    private static final String PROVIDER_NAME = "openai";

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean isAvailable() {
        AiProperties.OpenAi cfg = aiProperties.getOpenai();
        return aiProperties.isEnabled()
                && cfg.getApiKey() != null
                && !cfg.getApiKey().isBlank();
    }

    @Override
    public String call(String prompt) {
        AiProperties.OpenAi cfg = aiProperties.getOpenai();
        String url = cfg.getBaseUrl() + "/chat/completions";

        String requestBody = buildRequestBody(prompt, cfg);
        log.debug("Calling OpenAI API model={} prompt_length={}", cfg.getModel(), prompt.length());

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(cfg.getTimeoutSeconds()))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + cfg.getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(cfg.getTimeoutSeconds()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("OpenAI API error status={} body={}", response.statusCode(), response.body());
                throw new RuntimeException("OpenAI API returned status: " + response.statusCode());
            }

            return extractText(response.body());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("OpenAI API call interrupted", e);
        } catch (Exception e) {
            log.error("OpenAI API call failed: {}", e.getMessage());
            throw new RuntimeException("OpenAI API call failed: " + e.getMessage(), e);
        }
    }

    private String buildRequestBody(String prompt, AiProperties.OpenAi cfg) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("model", cfg.getModel());

            ArrayNode messages = objectMapper.createArrayNode();
            ObjectNode message = objectMapper.createObjectNode();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            root.set("messages", messages);

            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build OpenAI request body", e);
        }
    }

    private String extractText(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText("");
        } catch (Exception e) {
            log.error("Failed to parse OpenAI response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse OpenAI response", e);
        }
    }
}
