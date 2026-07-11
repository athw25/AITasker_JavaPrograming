package com.aitasker.recommendation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendationFeedbackRequest {

    @NotNull(message = "Job ID không được để trống")
    private Long jobId;

    @NotNull(message = "Expert ID không được để trống")
    private Long expertId;

    private boolean recommended;

    private boolean hired;
}
