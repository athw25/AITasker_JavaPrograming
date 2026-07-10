package com.aitasker.audit.controller;

import com.aitasker.audit.dto.AuditLogResponse;
import com.aitasker.audit.entity.AuditLog;
import com.aitasker.audit.service.AuditLogService;
import com.aitasker.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<AuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLog> logsPage = auditLogService.getLogs(email, action, pageable);
        Page<AuditLogResponse> responsePage = logsPage.map(AuditLogResponse::from);
        return ApiResponse.success("Get audit logs successfully", responsePage);
    }
}
