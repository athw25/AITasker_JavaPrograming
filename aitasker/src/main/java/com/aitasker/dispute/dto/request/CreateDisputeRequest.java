package com.aitasker.dispute.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDisputeRequest {
    @NotNull(message = "projectId không được để trống")
    private Long projectId;

    private Long milestoneId;

    @NotBlank(message = "reason không được để trống")
    private String reason;
}
