// AdminController.java
package com.aitasker.admin.controller;

import com.aitasker.admin.service.AdminService;
import com.aitasker.audit.service.AuditLogService;
import com.aitasker.common.response.ApiResponse;
import com.aitasker.common.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management endpoints")
public class AdminController {

    private final AdminService adminService;
    private final AuditLogService auditLogService;
    private final HttpServletRequest httpServletRequest;

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Delete user (Admin only)")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        String ipAddress = auditLogService.getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        
        try {
            adminService.deleteUser(userId);
            auditLogService.logAdminAction(getCurrentUserId(), getCurrentUserEmail(), "DELETE", "USER", userId,
                    "User deleted by admin", null, ipAddress, userAgent, "SUCCESS");
            return ResponseEntity.ok(ApiResponse.success("User deleted"));
        } catch (Exception e) {
            auditLogService.logAdminAction(getCurrentUserId(), getCurrentUserEmail(), "DELETE", "USER", userId,
                    "User deletion failed", e.getMessage(), ipAddress, userAgent, "FAILED");
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @PutMapping("/users/{userId}/role")
    @Operation(summary = "Update user role (Admin only)")
    public ResponseEntity<ApiResponse> updateUserRole(@PathVariable Long userId, @RequestParam String role) {
        String ipAddress = auditLogService.getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        
        try {
            adminService.updateUserRole(userId, role);
            auditLogService.logAdminAction(getCurrentUserId(), getCurrentUserEmail(), "UPDATE", "USER_ROLE", userId,
                    "User role updated to: " + role, null, ipAddress, userAgent, "SUCCESS");
            return ResponseEntity.ok(ApiResponse.success("Role updated"));
        } catch (Exception e) {
            auditLogService.logAdminAction(getCurrentUserId(), getCurrentUserEmail(), "UPDATE", "USER_ROLE", userId,
                    "Role update failed", e.getMessage(), ipAddress, userAgent, "FAILED");
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @PostMapping("/users/{userId}/lock")
    @Operation(summary = "Lock user account (Admin only)")
    public ResponseEntity<ApiResponse> lockUserAccount(@PathVariable Long userId) {
        String ipAddress = auditLogService.getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        
        try {
            adminService.lockUserAccount(userId);
            auditLogService.logAdminAction(getCurrentUserId(), getCurrentUserEmail(), "LOCK", "USER", userId,
                    "User account locked by admin", null, ipAddress, userAgent, "SUCCESS");
            return ResponseEntity.ok(ApiResponse.success("Account locked"));
        } catch (Exception e) {
            auditLogService.logAdminAction(getCurrentUserId(), getCurrentUserEmail(), "LOCK", "USER", userId,
                    "Account lock failed", e.getMessage(), ipAddress, userAgent, "FAILED");
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @PostMapping("/users/{userId}/unlock")
    @Operation(summary = "Unlock user account (Admin only)")
    public ResponseEntity<ApiResponse> unlockUserAccount(@PathVariable Long userId) {
        String ipAddress = auditLogService.getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        
        try {
            adminService.unlockUserAccount(userId);
            auditLogService.logAdminAction(getCurrentUserId(), getCurrentUserEmail(), "UNLOCK", "USER", userId,
                    "User account unlocked by admin", null, ipAddress, userAgent, "SUCCESS");
            return ResponseEntity.ok(ApiResponse.success("Account unlocked"));
        } catch (Exception e) {
            auditLogService.logAdminAction(getCurrentUserId(), getCurrentUserEmail(), "UNLOCK", "USER", userId,
                    "Account unlock failed", e.getMessage(), ipAddress, userAgent, "FAILED");
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "Get all audit logs (Admin only)")
    public ResponseEntity<ApiResponse> getAllAuditLogs() {
        try {
            return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    private Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private String getCurrentUserEmail() {
        return SecurityUtils.getCurrentUserEmail();
    }
}
