package com.aitasker.admin.controller;

import com.aitasker.admin.dto.UserSummaryResponse;
import com.aitasker.admin.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public List<UserSummaryResponse> getAllUsers() {
        return adminUserService.getAllUsers();
    }
}
