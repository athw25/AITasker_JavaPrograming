package com.aitasker.job.dto;

import com.aitasker.common.enums.JobStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class JobPostResponse {
    private Long id;
    private String title;
    private String description;
    private Double budget;
    private LocalDate deadline;
    private String requiredSkills;
    private JobStatus status;
    private Long ClientId;
    private String clientName;
}
