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
public class GeminiProvider implements AiProvider {

    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;

    private static final String PROVIDER_NAME = "gemini";

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean isAvailable() {
        AiProperties.Gemini cfg = aiProperties.getGemini();
        return aiProperties.isEnabled()
                && cfg.getApiKey() != null
                && !cfg.getApiKey().isBlank();
    }

    @Override
    public String call(String prompt) {
        AiProperties.Gemini cfg = aiProperties.getGemini();
        String url = String.format("%s/%s:generateContent?key=%s",
                cfg.getBaseUrl(), cfg.getModel(), cfg.getApiKey());

        String requestBody = buildRequestBody(prompt, cfg);
        log.debug("Calling Gemini API model={} prompt_length={}", cfg.getModel(), prompt.length());

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(cfg.getTimeoutSeconds()))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(cfg.getTimeoutSeconds()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Gemini API error status={} body={}", response.statusCode(), response.body());
                throw new RuntimeException("Gemini API returned status: " + response.statusCode());
            }

            return extractText(response.body());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Gemini API call interrupted", e);
        } catch (Exception e) {
            log.error("Gemini API call failed: {}", e.getMessage());
            throw new RuntimeException("Gemini API call failed: " + e.getMessage(), e);
        }
    }

    private String buildRequestBody(String prompt, AiProperties.Gemini cfg) {
        try {
            ObjectNode root = objectMapper.createObjectNode();

            ObjectNode generationConfig = objectMapper.createObjectNode();
            generationConfig.put("temperature", cfg.getTemperature());
            generationConfig.put("maxOutputTokens", cfg.getMaxOutputTokens());
            root.set("generationConfig", generationConfig);

            ArrayNode contents = objectMapper.createArrayNode();
            ObjectNode content = objectMapper.createObjectNode();
            ArrayNode parts = objectMapper.createArrayNode();
            ObjectNode part = objectMapper.createObjectNode();
            part.put("text", prompt);
            parts.add(part);
            content.set("parts", parts);
            contents.add(content);
            root.set("contents", contents);

            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build Gemini request body", e);
        }
    }

    private String extractText(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText("");
        } catch (Exception e) {
            log.error("Failed to parse Gemini response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse Gemini response", e);
        }
    }
}