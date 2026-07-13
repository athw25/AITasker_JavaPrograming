package com.aitasker.auth.controller;

import com.aitasker.security.userdetails.CustomUserDetails;
import com.aitasker.user.dto.UserProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(@AuthenticationPrincipal CustomUserDetails principal) {
        var user = principal.getUser();
        return ResponseEntity.ok(UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getName())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .build());
    }
}
