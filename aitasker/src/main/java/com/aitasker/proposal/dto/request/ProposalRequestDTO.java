// ProposalRequestDTO.java
package com.aitasker.proposal.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProposalRequestDTO {

    @NotNull(message = "Job ID không được để trống")
    private Long jobId;

    @NotNull(message = "Giá thầu không được để trống")
    @Min(value = 0, message = "Giá thầu không được âm")
    private Double bidAmount;

    @NotBlank(message = "Thư giới thiệu không được để trống")
    private String coverLetter;
}