package com.aitasker.auth.service;

import com.aitasker.audit.service.AuditLogService;
import com.aitasker.auth.dto.AuthResponse;
import com.aitasker.auth.dto.LoginRequest;
import com.aitasker.auth.dto.RegisterRequest;
import com.aitasker.common.enums.Role;
import com.aitasker.common.enums.UserStatus;
import com.aitasker.expert.repository.ExpertProfileRepository;
import com.aitasker.security.jwt.JwtService;
import com.aitasker.security.refreshtoken.entity.RefreshToken;
import com.aitasker.security.refreshtoken.repository.RefreshTokenRepository;
import com.aitasker.security.ratelimit.LoginAttemptService;
import com.aitasker.security.userdetails.CustomUserDetails;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ExpertProfileRepository expertProfileRepository;
    private final AuditLogService auditLogService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginAttemptService loginAttemptService;
    private final HttpServletRequest httpServletRequest;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email nay da duoc su dung!");
        }

        User user = new User();
        user.setName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);

        Role requestedRole;
        try {
            requestedRole = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role khong hop le. Phai la CLIENT hoac EXPERT");
        }

        if (requestedRole != Role.CLIENT && requestedRole != Role.EXPERT) {
            throw new IllegalArgumentException("Role khong hop le. Phai la CLIENT hoac EXPERT");
        }

        user.setRole(requestedRole);
        userRepository.save(user);

        String ipAddress = auditLogService.getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        auditLogService.logAction("REGISTER", "USER", user.getId(), user.getId(),
                user.getEmail(), "User registration", null, ipAddress, userAgent, "SUCCESS");

        if (user.getRole() == Role.EXPERT) {
            com.aitasker.expert.entity.ExpertProfile profile = new com.aitasker.expert.entity.ExpertProfile();
            profile.setUser(user);
            profile.setFullName(request.getFullName());
            expertProfileRepository.save(profile);
        }

        return AuthResponse.builder()
                .message("Dang ky thanh cong! Vui long dang nhap.")
                .token(null)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email hoac mat khau khong dung!"));

        if (Boolean.TRUE.equals(user.getAccountLocked())) {
            if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
                auditLogService.log(user, "LOGIN_BLOCKED", "Tai khoan dang bi khoa tam thoi");
                throw new IllegalArgumentException("Tai khoan tam thoi bi khoa do dang nhap sai nhieu lan!");
            }
            resetLoginLock(user);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            recordFailedLogin(user);
            throw new IllegalArgumentException("Email hoac mat khau khong dung!");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Tai khoan da bi khoa hoac vo hieu hoa!");
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String jwtToken = jwtService.generateToken(userDetails);
        com.aitasker.auth.entity.RefreshToken refreshToken = refreshTokenService.create(user);

        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        user.setLockedUntil(null);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        auditLogService.log(user, "LOGIN", "Dang nhap thanh cong");

        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken.getToken())
                .message("Dang nhap thanh cong!")
                .build();
    }

    @Override
    public AuthResponse refresh(String refreshTokenValue) {
        com.aitasker.auth.entity.RefreshToken refreshToken = refreshTokenService.verify(refreshTokenValue);
        User user = refreshToken.getUser();

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String jwtToken = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken.getToken())
                .message("Lam moi token thanh cong!")
                .build();
    }

    @Override
    public void logout(String refreshTokenValue) {
        refreshTokenService.revoke(refreshTokenValue);
    }

    private void recordFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() == null ? 1 : user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= maxFailedAttempts) {
            user.setAccountLocked(true);
            user.setLockedUntil(LocalDateTime.now().plusMinutes(lockMinutes));
            auditLogService.log(user, "LOGIN_LOCKED", "Khoa tam tai khoan do dang nhap sai nhieu lan");
        } else {
            auditLogService.log(user, "LOGIN_FAILED", "Dang nhap that bai");
        }
        userRepository.save(user);
    }

    private void resetLoginLock(User user) {
        user.setAccountLocked(false);
        user.setLockedUntil(null);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }
}
