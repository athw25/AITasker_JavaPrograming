package com.aitasker.recommendation.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RecommendationResponseDTO {
    private Long recommendationId;
    private Long expertId;
    private String expertName;
    private Long jobId;
    private Double matchScore;
    private Double skillScore;
    private Double ratingScore;
    private Double successRateScore;
    private Double experienceScore;
    private boolean isAccepted;
    private LocalDateTime createdAt;
}
