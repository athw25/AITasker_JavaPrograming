package com.aitasker.job.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.job.dto.JobPostRequest;
import com.aitasker.job.dto.JobPostResponse;
import com.aitasker.job.dto.JobSearchRequest;
import com.aitasker.job.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Job marketplace")
public class JobController {
    private final JobService jobService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Create a new job post")
    public ApiResponse<JobPostResponse> create(@RequestBody JobPostRequest request) {
        return ApiResponse.success(jobService.create(request));
    }

    @GetMapping
    @Operation(summary = "Get all job posts")
    public ApiResponse<List<JobPostResponse>> getAll() {
        return ApiResponse.success(jobService.getAll());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Get jobs created by the authenticated client")
    public ApiResponse<List<JobPostResponse>> getMyJobs() {
        return ApiResponse.success(jobService.getMyJobs());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job post by ID")
    public ApiResponse<JobPostResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(jobService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "update a job post")
    public ApiResponse<JobPostResponse> update(@PathVariable Long id, @RequestBody JobPostRequest request) {
        return ApiResponse.success(jobService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Delete a job post")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        jobService.delete(id);
        return ApiResponse.success("Đã xóa job", null);
    }

    @GetMapping("/search")
    @Operation(summary = "Search job post")
    public ApiResponse<List<JobPostResponse>> search(@ModelAttribute JobSearchRequest request) {
        return ApiResponse.success(jobService.search(request));
    }
}
