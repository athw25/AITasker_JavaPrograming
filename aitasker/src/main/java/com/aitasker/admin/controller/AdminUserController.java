package com.aitasker.admin.controller;

import com.aitasker.admin.dto.UserSummaryResponse;
import com.aitasker.admin.service.AdminUserService;
import com.aitasker.common.enums.Role;
import com.aitasker.common.response.ApiResponse;
import com.aitasker.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<UserSummaryResponse>> getAllUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(
                adminUserService.getAllUsers(keyword, role, page, size));
    }
    @PutMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> banUser(@PathVariable Long id) {
        adminUserService.banUser(id);
        return ApiResponse.success("BanUser successfully");
    }

    @PutMapping("/{id}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> unbanUser(@PathVariable Long id) {
        adminUserService.unbanUser(id);
        return ApiResponse.success("UnbanUser successfully");
    }
}