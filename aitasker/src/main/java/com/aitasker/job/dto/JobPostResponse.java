package com.aitasker.job.dto;

import com.aitasker.common.enums.JobStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class JobPostResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal budget;
    private LocalDate deadline;
    private String requiredSkills;
    private JobStatus status;
    // ClientId -> clientId: theo đúng quy ước đặt tên field Java (camelCase)
    private Long clientId;
    private String clientName;
}
