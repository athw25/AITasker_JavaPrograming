package com.aitasker.job.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class JobPostRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "Mô tả không được để trống")
    private String description;

    @NotNull @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal budget;

    @Future(message = "Deadline phải ở tương lai")
    private LocalDate deadline;

    @NotBlank(message = "Kỹ năng yêu cầu không được để trống")
    private String skills;
}
