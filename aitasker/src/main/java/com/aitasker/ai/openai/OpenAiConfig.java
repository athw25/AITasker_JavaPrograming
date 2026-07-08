package com.aitasker.ai.openai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.openai")
public record OpenAiConfig(
        String apiKey,
        String model,
        String baseUrl
) {
    public OpenAiConfig {
        if (model == null || model.isBlank()) model = "gpt-4o-mini";
        if (baseUrl == null || baseUrl.isBlank()) baseUrl = "https://api.openai.com/v1/chat/completions";
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }
}
