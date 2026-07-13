package com.aitasker.dispute.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.common.response.PageResponse;
import com.aitasker.dispute.dto.response.DisputeResponse;
import com.aitasker.dispute.service.DisputeService;
import com.aitasker.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/disputes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDisputeController {

    private final DisputeService disputeService;

    @GetMapping
    public ApiResponse<PageResponse<DisputeResponse>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success(disputeService.getDisputes(status, page, size, principal.getUser()));
    }
}