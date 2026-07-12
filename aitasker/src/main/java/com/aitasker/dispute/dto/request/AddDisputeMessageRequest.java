package com.aitasker.dispute.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddDisputeMessageRequest {

    @NotBlank(message = "message không được để trống")
    private String message;
}
