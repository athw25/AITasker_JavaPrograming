package com.aitasker.ai.provider;

import com.aitasker.ai.config.AiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OllamaProvider implements AiProvider {

    private final AiProperties aiProperties;

    private static final String PROVIDER_NAME = "ollama";

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean isAvailable() {
        return aiProperties.isEnabled()
                && PROVIDER_NAME.equals(aiProperties.getProvider());
    }

    @Override
    public String call(String prompt) {
        // TODO: implement Ollama local API integration when required
        log.warn("OllamaProvider is not yet implemented");
        throw new UnsupportedOperationException("Ollama provider not yet implemented. Use GeminiProvider.");
    }
}