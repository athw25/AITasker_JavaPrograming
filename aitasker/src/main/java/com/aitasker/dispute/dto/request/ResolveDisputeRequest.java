package com.aitasker.dispute.dto.request;

import com.aitasker.common.enums.DisputeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResolveDisputeRequest {
    @NotNull(message = "status không được để trống")
    private DisputeStatus status;

    @NotBlank(message = "resolution không được để trống")
    private String resolution;
}
