package com.aitasker.analytics.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class AnalyticsResponse {

    private double proposalAcceptanceRate;
    private double projectSuccessRate;
    private double avgProposalsPerJob;
    private double expertMatchConversionRate;

    private Map<String, Long> eventCountsByType;
    private List<String> topSkillsDemanded;

    private long totalAiPromptsJobAssistant;
    private long totalAiPromptsServiceGenerator;
    private double aiPromptAcceptanceRate;
}