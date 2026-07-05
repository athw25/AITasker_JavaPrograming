package com.aitasker.milestone.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMilestoneRequest {

    /**
     * Project chứa milestone.
     */
    @NotNull(message = "Project ID không được để trống.")
    private Long projectId;

    /**
     * Tiêu đề milestone.
     */
    @NotBlank(message = "Tiêu đề không được để trống.")
    @Size(
            max = 255,
            message = "Tiêu đề tối đa 255 ký tự."
    )
    private String title;

    /**
     * Mô tả milestone.
     */
    @NotBlank(message = "Mô tả không được để trống.")
    @Size(
            max = 3000,
            message = "Mô tả tối đa 3000 ký tự."
    )
    private String description;

    /**
     * Giá trị milestone.
     */
    @NotNull(message = "Số tiền không được để trống.")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Số tiền phải lớn hơn 0."
    )
    private BigDecimal amount;

    /**
     * Hạn hoàn thành.
     */
    @NotNull(message = "Hạn hoàn thành không được để trống.")
    @Future(message = "Hạn hoàn thành phải ở tương lai.")
    private LocalDate dueDate;
}