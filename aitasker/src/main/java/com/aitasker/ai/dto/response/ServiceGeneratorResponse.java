package com.aitasker.ai.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceGeneratorResponse {
    private String title;
    private String description;
    private List<String> tags;
    private BigDecimal suggestedPrice;
    private Integer deliveryDays;
}
