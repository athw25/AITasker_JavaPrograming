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
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OllamaProvider implements AiProvider {

    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;
    private static final String PROVIDER_NAME = "ollama";

    @Override
    public String getProviderName() { return PROVIDER_NAME; }

    @Override
    public boolean isAvailable() {
        return aiProperties.isEnabled() && PROVIDER_NAME.equals(aiProperties.getProvider());
    }

    @Override
    public String call(String prompt) {
        AiProperties.Ollama cfg = aiProperties.getOllama();
        try {
            String body = objectMapper.writeValueAsString(
                    Map.of("model", cfg.getModel(), "prompt", prompt, "stream", false));
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(cfg.getTimeoutSeconds())).build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(cfg.getBaseUrl() + "/api/generate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode node = objectMapper.readTree(response.body());
            return node.path("response").asText("");
        } catch (Exception e) {
            log.error("Ollama call failed: {}", e.getMessage());
            return "";
        }
    }
}
