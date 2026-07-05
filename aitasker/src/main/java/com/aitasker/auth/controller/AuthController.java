package com.aitasker.audit.controller;

import com.aitasker.audit.entity.AuditLog;
import com.aitasker.audit.service.AuditLogService;
import com.aitasker.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@Tag(name = "Audit Log", description = "Audit log management endpoints")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/logs/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs for a specific user")
    public ResponseEntity<ApiResponse> getAuditLogsByUser(@PathVariable Long userId) {
        List<AuditLog> logs = auditLogService.getAuditLogsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/logs/action/{actionType}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs by action type")
    public ResponseEntity<ApiResponse> getAuditLogsByActionType(@PathVariable String actionType) {
        List<AuditLog> logs = auditLogService.getAuditLogsByActionType(actionType);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/logs/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs within a date range")
    public ResponseEntity<ApiResponse> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AuditLog> logs = auditLogService.getAuditLogsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/logs/user/{userId}/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs for user within a date range")
    public ResponseEntity<ApiResponse> getAuditLogsByUserAndDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AuditLog> logs = auditLogService.getAuditLogsByUserAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/logs/entity/{entityType}/{entityId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs for a specific entity")
    public ResponseEntity<ApiResponse> getAuditLogsByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        List<AuditLog> logs = auditLogService.getAuditLogsByEntity(entityType, entityId);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}