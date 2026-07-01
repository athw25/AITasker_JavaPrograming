package com.aitasker.admin.controller;

import com.aitasker.admin.service.AdminUserService;
import com.aitasker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public List<User> getAllUsers() {
        return adminUserService.getAllUsers();
    }
}