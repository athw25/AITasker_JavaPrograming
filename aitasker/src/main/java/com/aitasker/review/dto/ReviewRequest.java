package com.aitasker.review.dto;

import com.aitasker.common.enums.ReviewType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Min(value = 1, message = "rating phải từ 1 đến 5")
    @Max(value = 5, message = "rating phải từ 1 đến 5")
    private Integer rating;

    @Size(max = 2000, message = "comment tối đa 2000 ký tự")
    private String comment;

    @NotNull(message = "type không được để trống")
    private ReviewType type;
}
