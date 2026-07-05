package com.aitasker.ai.assistant;
import com.aitasker.ai.dto.response.ServiceGeneratorResponse;
import org.springframework.stereotype.Service;
@Service



public interface ServiceGeneratorService {
    ServiceGeneratorResponse generateServicePackage(String userPrompt);
}