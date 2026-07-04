package com.aitasker.job.dto;

import com.aitasker.common.enums.JobStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class JobSearchRequest {
    private String keyword;
    private String skills;
    private BigDecimal minBudget;
    private BigDecimal maxBudget;
    private JobStatus status;
}
