// AuthController.java
package com.aitasker.auth.controller;

import com.aitasker.auth.dto.AuthResponse;
import com.aitasker.auth.dto.LoginRequest;
import com.aitasker.auth.dto.RegisterRequest;
import com.aitasker.auth.service.AuthService;
import com.aitasker.audit.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuditLogService auditLogService;
    private final HttpServletRequest httpServletRequest;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        String ipAddress = auditLogService.getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        
        try {
            AuthResponse response = authService.register(request);
            auditLogService.logAction("REGISTER", "USER", null, null, request.getEmail(), 
                    "User registration attempt", null, ipAddress, userAgent, "SUCCESS");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            auditLogService.logAction("REGISTER", "USER", null, null, request.getEmail(), 
                    "User registration failed", e.getMessage(), ipAddress, userAgent, "FAILED");
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String ipAddress = auditLogService.getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            auditLogService.logAction("LOGIN", "USER", null, null, request.getEmail(), 
                    "Login failed", e.getMessage(), ipAddress, userAgent, "FAILED");
            throw e;
        }
    }
    // review: integration chưa hoàn tất
}