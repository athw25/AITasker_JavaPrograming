package com.aitasker.admin.controller;

import com.aitasker.admin.dto.UserSummaryResponse;
import com.aitasker.admin.service.AdminUserService;
import com.aitasker.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public List<UserSummaryResponse> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    @PutMapping("/{id}/ban")
    public ApiResponse<UserSummaryResponse> banUser(@PathVariable Long id) {
        return ApiResponse.success(adminUserService.banUser(id));
    }

    @PutMapping("/{id}/unban")
    public ApiResponse<UserSummaryResponse> unbanUser(@PathVariable Long id) {
        return ApiResponse.success(adminUserService.unbanUser(id));
    }
}
