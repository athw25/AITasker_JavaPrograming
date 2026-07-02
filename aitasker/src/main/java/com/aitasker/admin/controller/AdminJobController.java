package com.aitasker.admin.controller;

import com.aitasker.admin.service.AdminJobService;
import com.aitasker.job.entity.JobPost;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/jobs")
@RequiredArgsConstructor
public class AdminJobController {
    private final AdminJobService adminJobService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<JobPost> getAllJobs(){
        return adminJobService.getAllJobs();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteJob(@PathVariable Long id){
        adminJobService.deleteJob(id);
    }
}
