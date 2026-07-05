package com.aitasker.ai.provider;

import com.aitasker.ai.config.AiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenAiProvider implements AiProvider {

    private final AiProperties aiProperties;

    private static final String PROVIDER_NAME = "openai";

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean isAvailable() {
        if (!aiProperties.isEnabled() || !PROVIDER_NAME.equals(aiProperties.getProvider())) {
            return false;
        }

        Object cfg = null;
        for (String methodName : new String[] { "getOpenAi", "getOpenAI", "getOpenai" }) {
            try {
                cfg = aiProperties.getClass().getMethod(methodName).invoke(aiProperties);
                break;
            } catch (ReflectiveOperationException ignored) {
                // try next method name variation
            }
        }

        if (cfg == null) {
            return false;
        }

        try {
            Object apiKey = cfg.getClass().getMethod("getApiKey").invoke(cfg);
            return apiKey instanceof String && !((String) apiKey).isBlank();
        } catch (ReflectiveOperationException e) {
            log.warn("Unable to access OpenAI API key configuration", e);
            return false;
        }
    }

    @Override
    public String call(String prompt) {
        // TODO: implement OpenAI API integration when required
        log.warn("OpenAiProvider is not yet implemented");
        throw new UnsupportedOperationException("OpenAI provider not yet implemented. Use GeminiProvider.");
    }
}