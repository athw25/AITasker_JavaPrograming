package com.aitasker.job.dto;

import com.aitasker.common.enums.JobStatus;
import lombok.Setter;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Setter
public class JobPostRequest {
    private String title;
    private String description;
    private Double budget;
    private LocalDate deadline;
    private String requiredSkills;
}
