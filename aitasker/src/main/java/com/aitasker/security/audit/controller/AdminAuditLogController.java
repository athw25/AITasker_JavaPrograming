package com.aitasker.security.audit.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.security.audit.entity.AuditLog;
import com.aitasker.security.audit.enums.AuditAction;
import com.aitasker.security.audit.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Audit Log Module", description = "Nhật ký bảo mật: Login, Payment, Dispute, Admin Actions")
public class AdminAuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @Operation(summary = "Tìm kiếm/lọc Audit Log theo action và actor")
    public ApiResponse<Page<AuditLog>> search(
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) Long actorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<AuditLog> result = auditLogService.search(
                action, actorId,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );
        return ApiResponse.success(result);
    }
}
