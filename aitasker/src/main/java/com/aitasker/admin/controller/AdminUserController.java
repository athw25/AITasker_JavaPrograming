package com.aitasker.admin.controller;

import com.aitasker.admin.service.AdminUserService;
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
    public List<User> getAllUsers() {
        return adminUserService.getAllUsers();
    }
    @PutMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public void banUser(@PathVariable Long id){
        adminUserService.banUser(id);
    }
    @PutMapping("/{id}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public void unbanUser(@PathVariable Long id){
        adminUserService.unbanUser(id);
    }
}