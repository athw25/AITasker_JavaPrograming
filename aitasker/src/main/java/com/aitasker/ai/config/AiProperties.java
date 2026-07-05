package com.aitasker.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ai")
@Getter
@Setter
public class AiProperties {

    private boolean enabled = false;
    private String provider = "gemini";

    private Gemini gemini = new Gemini();
    private OpenAi openai = new OpenAi();
    private Ollama ollama = new Ollama();

    @Getter
    @Setter
    public static class Gemini {
        private String apiKey = "";
        private String model = "gemini-2.0-flash";
        private String baseUrl = "https://generativelanguage.googleapis.com/v1beta/models";
        private int timeoutSeconds = 30;
        private double temperature = 0.7;
        private int maxOutputTokens = 2048;
    }

    @Getter
    @Setter
    public static class OpenAi {
        private String apiKey = "";
        private String model = "gpt-4o-mini";
        private String baseUrl = "https://api.openai.com/v1";
        private int timeoutSeconds = 30;
    }

    @Getter
    @Setter
    public static class Ollama {
        private String baseUrl = "http://localhost:11434";
        private String model = "llama3";
        private int timeoutSeconds = 60;
    }
}