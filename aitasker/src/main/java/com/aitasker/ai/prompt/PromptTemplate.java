package com.aitasker.ai.prompt;

public enum PromptTemplate {

    JOB_ASSISTANT("""
            You are an AI assistant helping a non-technical client create a job post on an AI services marketplace.
            Client input: "%s"
            Analyze the input and generate a professional job post.
            Respond ONLY in valid JSON with this exact structure, no markdown, no explanation:
            {
              "title": "...",
              "description": "...",
              "skills": ["skill1", "skill2", "skill3"],
              "budgetSuggestion": "..."
            }
            """),

    SERVICE_GENERATOR("""
            You are an AI assistant helping an AI expert create a service listing on a marketplace.
            Expert input: "%s"
            Analyze the input and generate a professional service description.
            Respond ONLY in valid JSON with this exact structure, no markdown, no explanation:
            {
              "serviceDescription": "...",
              "tags": ["tag1", "tag2", "tag3"],
              "pricingSuggestion": "..."
            }
            """),

    EXPERT_RECOMMENDATION("""
            You are an AI matching engine for an AI services marketplace.
            Job requirements: %s
            Expert profile: %s
            Analyze compatibility and return ONLY a JSON score object, no explanation:
            {
              "matchScore": 0,
              "reasoning": "..."
            }
            """);

    private final String template;

    PromptTemplate(String template) {
        this.template = template;
    }

    public String format(Object... args) {
        return template.formatted(args);
    }

    public String getTemplate() {
        return template;
    }
}