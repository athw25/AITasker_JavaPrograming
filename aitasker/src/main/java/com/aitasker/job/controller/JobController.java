package com.aitasker.job.controller;

import com.aitasker.job.dto.JobPostRequest;
import com.aitasker.job.dto.JobPostResponse;
import com.aitasker.job.dto.JobSearchRequest;
import com.aitasker.job.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<JobPostResponse> create (@RequestBody JobPostRequest request){
        return ResponseEntity.ok(jobService.create(request));
    }

    @GetMapping
    @Operation(summary = "Get all ob posts")
    public ResponseEntity<List<JobPostResponse>> getAll(){
        return ResponseEntity.ok(jobService.getAll());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Get jobs created by the authenticated client")
    public ResponseEntity<List<JobPostResponse>> getMyJobs() {
        return ResponseEntity.ok(jobService.getMyJobs());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get job post by ID")
    public ResponseEntity<JobPostResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "update a job post")
    public ResponseEntity<JobPostResponse> update(@PathVariable Long id,@RequestBody JobPostRequest request){
        return ResponseEntity.ok(jobService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Delete a job post")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        jobService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search job post")
    public ResponseEntity<List<JobPostResponse>> search(@ModelAttribute JobSearchRequest request){
        return ResponseEntity.ok(jobService.search(request));
    }
}
