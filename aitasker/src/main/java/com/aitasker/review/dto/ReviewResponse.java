package com.aitasker.review.dto;

import com.aitasker.common.enums.ReviewType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponse {
    private Long id;
    private Long reviewerId;
    private String reviewerName;
    private Long revieweeId;
    private String revieweeName;
    private Long projectId;
    private Integer rating;
    private String comment;
    private ReviewType type;
    private LocalDateTime createdAt;
}
