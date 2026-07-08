package com.aitasker.audit.controller;

import com.aitasker.audit.entity.AuditLog;
import com.aitasker.audit.repository.AuditLogRepository;
import com.aitasker.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public ApiResponse<List<AuditLog>> getAll() {
        return ApiResponse.success(auditLogRepository.findAll());
    }
}
