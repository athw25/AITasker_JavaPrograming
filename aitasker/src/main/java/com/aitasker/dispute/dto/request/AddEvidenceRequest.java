package com.aitasker.dispute.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddEvidenceRequest {

    @NotBlank(message = "fileUrl không được để trống")
    private String fileUrl;

    private String description;
}