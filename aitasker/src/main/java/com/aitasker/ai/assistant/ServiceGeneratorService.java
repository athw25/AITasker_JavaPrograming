package com.aitasker.ai.assistant;

import com.aitasker.ai.dto.response.ServiceGeneratorResponse;

public interface ServiceGeneratorService {
    ServiceGeneratorResponse generateServicePackage(String userPrompt);
}