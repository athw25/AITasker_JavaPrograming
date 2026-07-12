package com.aitasker.dispute.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.common.response.PageResponse;
import com.aitasker.dispute.dto.request.AddDisputeMessageRequest;
import com.aitasker.dispute.dto.request.AddEvidenceRequest;
import com.aitasker.dispute.dto.request.CreateDisputeRequest;
import com.aitasker.dispute.dto.request.DisputeResolveRequest;
import com.aitasker.dispute.dto.response.DisputeResponse;
import com.aitasker.dispute.service.DisputeService;
import com.aitasker.security.userdetails.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/disputes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Disputes", description = "Dispute management APIs")
public class DisputeController {

    private final DisputeService disputeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CLIENT', 'EXPERT')")
    @Operation(summary = "Create a dispute for a project")
    public ApiResponse<DisputeResponse> create(
            @Valid @RequestBody CreateDisputeRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success(
                "Dispute created successfully",
                disputeService.createDispute(request, principal.getUser()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'EXPERT', 'ADMIN')")
    @Operation(summary = "List disputes (optionally filter by status)")
    public ApiResponse<PageResponse<DisputeResponse>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success(
                "Disputes retrieved successfully",
                disputeService.getDisputes(status, page, size, principal.getUser()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'EXPERT', 'ADMIN')")
    @Operation(summary = "Get dispute detail by id")
    public ApiResponse<DisputeResponse> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success(
                "Dispute retrieved successfully",
                disputeService.getDisputeById(id, principal.getUser()));
    }

    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Resolve or reject a dispute (Admin only)")
    public ApiResponse<DisputeResponse> resolve(
            @PathVariable Long id,
            @Valid @RequestBody DisputeResolveRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success(
                "Dispute resolved successfully",
                disputeService.resolveDispute(id, request, principal.getUser()));
    }

    @PostMapping("/{id}/evidence")
    @PreAuthorize("hasAnyRole('CLIENT', 'EXPERT')")
    @Operation(summary = "Add evidence to an open dispute")
    public ApiResponse<DisputeResponse> addEvidence(
            @PathVariable Long id,
            @Valid @RequestBody AddEvidenceRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success(
                "Evidence added successfully",
                disputeService.addEvidence(id, request, principal.getUser()));
    }

    @PostMapping("/{id}/messages")
    @PreAuthorize("hasAnyRole('CLIENT', 'EXPERT', 'ADMIN')")
    @Operation(summary = "Send a message on a dispute")
    public ApiResponse<DisputeResponse> addMessage(
            @PathVariable Long id,
            @Valid @RequestBody AddDisputeMessageRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success(
                "Message sent successfully",
                disputeService.addMessage(id, request, principal.getUser()));
    }
}