package com.aitasker.ai.assistant;
import org.springframework.stereotype.Service;
import com.aitasker.ai.dto.response.JobAssistantResponse;
@Service




public interface JobAssistantService {
    JobAssistantResponse generateJobDescription(String userPrompt);
}