package com.aitasker.ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpertRecommendationResponse {
    private Long expertId;
    private String expertName;
    private double matchScore;
}
