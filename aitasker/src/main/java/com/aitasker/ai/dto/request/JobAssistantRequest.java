package com.aitasker.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobAssistantRequest {
    @NotBlank(message = "prompt không được để trống")
    private String prompt;
}
