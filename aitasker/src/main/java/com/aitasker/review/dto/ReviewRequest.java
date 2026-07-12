package com.aitasker.review.dto;

import com.aitasker.common.enums.ReviewType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {
    @NotNull(message = "revieweeId không được để trống")
    private Long revieweeId;

    @NotNull(message = "projectId không được để trống")
    private Long projectId;

    @NotNull(message = "rating không được để trống")
    @Min(value = 1, message = "rating tối thiểu là 1")
    @Max(value = 5, message = "rating tối đa là 5")
    private Integer rating;

    private String comment;

    @NotNull(message = "type không được để trống")
    private ReviewType type;
}
