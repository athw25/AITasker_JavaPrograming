package com.aitasker.analytics.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MonthlyDataPoint {
    private String month;
    private long count;
    private BigDecimal amount;
}