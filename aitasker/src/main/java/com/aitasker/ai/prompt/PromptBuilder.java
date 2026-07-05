package com.aitasker.ai.prompt;

import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    public String buildJobAssistantPrompt(String userInput) {
        return PromptTemplate.JOB_ASSISTANT.format(sanitize(userInput));
    }

    public String buildServiceGeneratorPrompt(String userInput) {
        return PromptTemplate.SERVICE_GENERATOR.format(sanitize(userInput));
    }

    public String buildExpertRecommendationPrompt(String jobRequirements, String expertProfile) {
        return PromptTemplate.EXPERT_RECOMMENDATION.format(
                sanitize(jobRequirements),
                sanitize(expertProfile)
        );
    }

    private String sanitize(String input) {
        if (input == null) return "";
        return input.replace("\"", "'").replace("\n", " ").trim();
    }
}