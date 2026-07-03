package com.aitasker.milestone.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.milestone.dto.request.CreateMilestoneRequest;
import com.aitasker.milestone.dto.request.SubmitMilestoneRequest;
import com.aitasker.milestone.dto.request.UpdateMilestoneRequest;
import com.aitasker.milestone.dto.response.MilestoneResponse;
import com.aitasker.milestone.service.MilestoneService;
import com.aitasker.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/milestones")
@RequiredArgsConstructor
@Validated
@Tag(name = "Milestones", description = "Milestone planning and review APIs")
public class MilestoneController {
    private final MilestoneService milestoneService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Create a project milestone")
    public ApiResponse<MilestoneResponse> create(@Valid @RequestBody CreateMilestoneRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success("Milestone created successfully",
                milestoneService.createMilestone(request, principal.getUser()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Update a pending or rejected milestone")
    public ApiResponse<MilestoneResponse> update(@PathVariable Long id,
            @Valid @RequestBody UpdateMilestoneRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success("Milestone updated successfully",
                milestoneService.updateMilestone(id, request, principal.getUser()));
    }

    @PutMapping("/{id}/submit")
    @PreAuthorize("hasRole('EXPERT')")
    @Operation(summary = "Submit a delivery version for a milestone")
    public ApiResponse<MilestoneResponse> submit(@PathVariable Long id,
            @Valid @RequestBody SubmitMilestoneRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success("Milestone submitted successfully",
                milestoneService.submitMilestone(id, request, principal.getUser()));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Approve the latest milestone delivery")
    public ApiResponse<MilestoneResponse> approve(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success("Milestone approved successfully",
                milestoneService.approveMilestone(id, principal.getUser()));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Reject the latest milestone delivery")
    public ApiResponse<MilestoneResponse> reject(@PathVariable Long id,
            @RequestParam @NotBlank @Size(max = 1000) String reason,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success("Milestone rejected successfully",
                milestoneService.rejectMilestone(id, reason, principal.getUser()));
    }

    @PutMapping("/{id}/release-payment")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Release payment for an approved milestone")
    public ApiResponse<MilestoneResponse> releasePayment(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success("Payment released successfully",
                milestoneService.releasePayment(id, principal.getUser()));
    }
}
