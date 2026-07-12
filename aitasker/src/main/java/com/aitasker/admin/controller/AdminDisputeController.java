package com.aitasker.admin.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.dispute.entity.Dispute;
import com.aitasker.dispute.repository.DisputeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/disputes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Disputes", description = "Admin xem và quản lý tranh chấp")
public class AdminDisputeController {

    private final DisputeRepository disputeRepository;

    @GetMapping
    @Operation(summary = "Xem tất cả dispute trên hệ thống")
    public ApiResponse<Page<Dispute>> getAllDisputes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success("OK", disputeRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }
}
