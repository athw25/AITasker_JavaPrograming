package com.aitasker.milestone.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Submit a new delivery version for a milestone")
public class SubmitMilestoneRequest {
    @NotBlank @Size(max = 1000)
    private String fileUrl;
    @Size(max = 5000)
    private String note;
}
