package com.aitasker.ai.gateway;

import com.aitasker.ai.config.AiProperties;
import com.aitasker.ai.provider.AiProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AiGateway {

    private final AiProperties aiProperties;
    private final Map<String, AiProvider> providers;

    public AiGateway(AiProperties aiProperties, List<AiProvider> providerList) {
        this.aiProperties = aiProperties;
        this.providers = providerList.stream()
                .collect(Collectors.toMap(AiProvider::getProviderName, Function.identity()));
    }

    public String call(String prompt) {
        if (!aiProperties.isEnabled()) {
            log.debug("AI is disabled. Returning empty response.");
            return "";
        }

        AiProvider provider = resolveProvider();
        if (provider == null || !provider.isAvailable()) {
            log.warn("No available AI provider found for configured provider: {}", aiProperties.getProvider());
            return "";
        }

        log.debug("Routing AI call to provider: {}", provider.getProviderName());
        return provider.call(prompt);
    }

    public boolean isAvailable() {
        if (!aiProperties.isEnabled()) return false;
        AiProvider provider = resolveProvider();
        return provider != null && provider.isAvailable();
    }

    public String getActiveProviderName() {
        AiProvider provider = resolveProvider();
        return provider != null ? provider.getProviderName() : "none";
    }

    private AiProvider resolveProvider() {
        String configured = aiProperties.getProvider();
        AiProvider provider = providers.get(configured);
        if (provider != null) return provider;

        return providers.values().stream()
                .filter(AiProvider::isAvailable)
                .findFirst()
                .orElse(null);
    }
}