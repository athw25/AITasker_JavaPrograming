package com.aitasker.ai.assistant;

import com.aitasker.ai.dto.response.JobAssistantResponse;

public interface JobAssistantService {
    JobAssistantResponse generateJobDescription(String userPrompt);
}