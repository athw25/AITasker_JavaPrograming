package com.aitasker.ai.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobAssistantResponse {
    private String title;
    private String description;
    private List<String> skills;
    private BigDecimal estimatedBudget;
    private String currency;
}
