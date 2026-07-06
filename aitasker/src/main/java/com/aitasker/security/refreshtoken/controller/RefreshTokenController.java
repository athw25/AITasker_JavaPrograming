package com.aitasker.security.refreshtoken.controller;

import com.aitasker.security.jwt.JwtService;
import com.aitasker.security.refreshtoken.entity.RefreshToken;
import com.aitasker.security.refreshtoken.repository.RefreshTokenRepository;
import com.aitasker.security.userdetails.CustomUserDetails;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import com.aitasker.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/token")
@RequiredArgsConstructor
@Tag(name = "Token Management", description = "Token refresh and management endpoints")
public class RefreshTokenController {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generate a new access token using a valid refresh token")
    public ResponseEntity<ApiResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.fail("Refresh token is required"));
        }

        Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByToken(refreshToken);

        if (tokenOptional.isEmpty() || tokenOptional.get().isExpired() || Boolean.TRUE.equals(tokenOptional.get().getRevoked())) {
            return ResponseEntity.ok(ApiResponse.fail("Invalid or expired refresh token"));
        }

        RefreshToken token = tokenOptional.get();
        User user = token.getUser();

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtService.generateToken(userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        response.put("expiresIn", 86400); // 24 hours in seconds

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/revoke")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Revoke refresh token")
    public ResponseEntity<ApiResponse> revokeToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.fail("Refresh token is required"));
        }

        Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByToken(refreshToken);

        if (tokenOptional.isPresent()) {
            RefreshToken token = tokenOptional.get();
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            return ResponseEntity.ok(ApiResponse.success("Token revoked successfully"));
        }

        return ResponseEntity.ok(ApiResponse.fail("Token not found"));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify refresh token validity")
    public ResponseEntity<ApiResponse> verifyToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.fail("Refresh token is required"));
        }

        Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByToken(refreshToken);

        if (tokenOptional.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(false));
        }

        RefreshToken token = tokenOptional.get();
        boolean isValid = !token.isExpired() && !Boolean.TRUE.equals(token.getRevoked());

        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);
        response.put("expiresAt", token.getExpiryDate());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

