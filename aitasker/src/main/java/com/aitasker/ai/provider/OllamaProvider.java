package com.aitasker.ai.provider;

import com.aitasker.ai.config.AiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class OllamaProvider implements AiProvider {

    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;

    private static final String PROVIDER_NAME = "ollama";

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean isAvailable() {
        AiProperties.Ollama cfg = aiProperties.getOllama();
        return aiProperties.isEnabled()
                && cfg.getBaseUrl() != null
                && !cfg.getBaseUrl().isBlank();
    }

    @Override
    public String call(String prompt) {
        AiProperties.Ollama cfg = aiProperties.getOllama();
        String url = cfg.getBaseUrl() + "/api/generate";

        String requestBody = buildRequestBody(prompt, cfg);
        log.debug("Calling Ollama API model={} prompt_length={}", cfg.getModel(), prompt.length());

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
                log.error("Ollama API error status={} body={}", response.statusCode(), response.body());
                throw new RuntimeException("Ollama API returned status: " + response.statusCode());
            }

            return extractText(response.body());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Ollama API call interrupted", e);
        } catch (Exception e) {
            log.error("Ollama API call failed: {}", e.getMessage());
            throw new RuntimeException("Ollama API call failed: " + e.getMessage(), e);
        }
    }

    private String buildRequestBody(String prompt, AiProperties.Ollama cfg) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("model", cfg.getModel());
            root.put("prompt", prompt);
            root.put("stream", false);
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build Ollama request body", e);
        }
    }

    private String extractText(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("response").asText("");
        } catch (Exception e) {
            log.error("Failed to parse Ollama response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse Ollama response", e);
        }
    }
}
