package com.aitasker.admin.controller;

import com.aitasker.admin.service.AdminJobService;
import com.aitasker.common.response.ApiResponse;
import com.aitasker.job.dto.JobPostResponse;
import com.aitasker.job.entity.JobPost;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/jobs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminJobController {

    private final AdminJobService adminJobService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<JobPostResponse>> getAllJobs(){
        return ApiResponse.success(adminJobService.getAllJobs());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteJob(@PathVariable Long id){
        adminJobService.deleteJob(id);

        return ApiResponse.success("Delete job successfully");
    }
}
