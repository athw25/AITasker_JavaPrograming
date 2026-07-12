package com.aitasker.dispute.controller;

import com.aitasker.common.enums.DisputeStatus;
import com.aitasker.common.response.ApiResponse;
import com.aitasker.dispute.dto.request.CreateDisputeRequest;
import com.aitasker.dispute.dto.request.ResolveDisputeRequest;
import com.aitasker.dispute.dto.response.DisputeResponse;
import com.aitasker.dispute.service.DisputeService;
import com.aitasker.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disputes")
@RequiredArgsConstructor
@Tag(name = "Dispute", description = "Quản lý tranh chấp Project/Milestone")
public class DisputeController {

    private final DisputeService disputeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENT','EXPERT')")
    public ApiResponse<DisputeResponse> create(
            @Valid @RequestBody CreateDisputeRequest request,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ApiResponse.success(disputeService.create(request, principal.getUser()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<DisputeResponse>> getAll(
            @RequestParam(required = false) DisputeStatus status
    ) {
        return ApiResponse.success(disputeService.getAll(status));
    }

    @GetMapping("/{id}")
    public ApiResponse<DisputeResponse> getDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ApiResponse.success(disputeService.getDetail(id, principal.getUser().getId(), principal.getUser().getRole() == com.aitasker.common.enums.Role.ADMIN));
    }

    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DisputeResponse> resolve(
            @PathVariable Long id,
            @Valid @RequestBody ResolveDisputeRequest request
    ) {
        return ApiResponse.success(disputeService.resolve(id, request));
    }
}
