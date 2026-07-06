package com.aitasker.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceGeneratorRequest {
    @NotBlank(message = "prompt không được để trống")
    private String prompt;
}
