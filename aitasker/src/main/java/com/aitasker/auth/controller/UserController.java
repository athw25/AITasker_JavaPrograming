package com.aitasker.auth.controller;

import com.aitasker.common.enums.Role;
import com.aitasker.common.enums.UserStatus;
import com.aitasker.security.userdetails.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Getter
    @AllArgsConstructor
    public static class MeResponse {
        private Long id;
        private String name;
        private String email;
        private Role role;
        private UserStatus status;
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal CustomUserDetails principal) {
        var user = principal.getUser();
        return new MeResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getStatus());
    }
}