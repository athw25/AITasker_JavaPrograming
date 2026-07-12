package com.aitasker.admin.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.job.entity.JobPost;
import com.aitasker.job.repository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/jobs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminJobController {

    private final JobPostRepository jobPostRepository;

    @GetMapping
    public ApiResponse<List<JobPost>> getAllJobs() {
        return ApiResponse.success(jobPostRepository.findAll());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteJob(@PathVariable Long id) {
        jobPostRepository.deleteById(id);
        return ApiResponse.success("Đã xóa job", null);
    }
}
