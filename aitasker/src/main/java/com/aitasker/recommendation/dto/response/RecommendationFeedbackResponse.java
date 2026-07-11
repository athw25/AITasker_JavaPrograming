package com.aitasker.recommendation.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class RecommendationFeedbackResponse {
    private Long feedbackId;
    private Long jobId;
    private Long expertId;
    private String expertName;
    private boolean recommended;
    private boolean hired;
    private LocalDateTime createdAt;
}
