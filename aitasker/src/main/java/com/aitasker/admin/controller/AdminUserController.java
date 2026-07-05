package com.aitasker.admin.controller;

import com.aitasker.admin.dto.UserSummaryResponse;
import com.aitasker.admin.service.AdminUserService;
import com.aitasker.common.response.ApiResponse;
import com.aitasker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserSummaryResponse>> getAllUsers() {
        return ApiResponse.success(
                adminUserService.getAllUsers());
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