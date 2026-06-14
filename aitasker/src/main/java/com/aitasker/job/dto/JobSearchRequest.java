package com.aitasker.job.dto;

import com.aitasker.common.enums.JobStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobSearchRequest {
    private String keyword;
    private String skills;
    private Double minBudget;
    private Double maxBudget;
    private JobStatus status;
}
