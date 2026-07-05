package com.aitasker.job.dto;

import com.aitasker.common.enums.JobStatus;
import lombok.Setter;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class JobPostRequest {
    private String title;
    private String description;
    // Double -> BigDecimal: khớp kiểu với JobPost.budget, tránh phải convert
    // qua lại và mất độ chính xác khi tính tiền.
    private BigDecimal budget;
    private LocalDate deadline;
    private String requiredSkills;
}
