package com.aitasker.ai.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@EnableConfigurationProperties(OpenAiConfig.class)
public class OpenAiClient {

    private final OpenAiConfig config;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAiClient(OpenAiConfig config) {
        this.config = config;
    }

    public boolean isAvailable() {
        return config.isConfigured();
    }

    public String chat(String systemPrompt, String userPrompt) {
        if (!config.isConfigured()) {
            throw new IllegalStateException("OpenAI API key chưa được cấu hình");
        }

        Map<String, Object> body = Map.of(
                "model", config.model(),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "temperature", 0.7
        );

        try {
            String json = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.baseUrl()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + config.apiKey())
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 300) {
                log.warn("OpenAI API trả lỗi status={}", response.statusCode());
                throw new IllegalStateException("OpenAI API lỗi: " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            return root.path("choices").path(0).path("message").path("content").asText();

        } catch (Exception e) {
            log.error("Lỗi gọi OpenAI API", e);
            throw new IllegalStateException("Không thể gọi OpenAI API: " + e.getMessage(), e);
        }
    }
}
