package com.aitasker.ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobAssistantResponse {
    private String title;
    private String description;
    private List<String> skills;
    private BigDecimal budget;
}
