package com.aitasker.job.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
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
    private String title;

    @NotBlank(message = "Mô tả không được để trống")
    private String description;

    // Double -> BigDecimal: khớp kiểu với JobPost.budget, tránh phải convert
    // qua lại và mất độ chính xác khi tính tiền.
    @NotNull(message = "Ngân sách không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Ngân sách phải lớn hơn 0")
    private BigDecimal budget;

    @NotNull(message = "Hạn chót không được để trống")
    @FutureOrPresent(message = "Hạn chót không được ở trong quá khứ")
    private LocalDate deadline;

    @NotBlank(message = "Kỹ năng yêu cầu không được để trống")
    private String requiredSkills;
}
