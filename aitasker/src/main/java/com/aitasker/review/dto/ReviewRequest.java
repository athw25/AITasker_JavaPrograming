package com.aitasker.review.dto;

import com.aitasker.common.enums.ReviewType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {
    private Long revieweeId;
    private Long projectId;
    private Integer rating;
    private String comment;
    private ReviewType type;
}
