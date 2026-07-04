package com.aitasker.auth.service;

import com.aitasker.auth.dto.AuthResponse;
import com.aitasker.auth.dto.LoginRequest;
import com.aitasker.auth.dto.RegisterRequest;
import com.aitasker.common.enums.Role;
import com.aitasker.security.jwt.JwtService;
import com.aitasker.security.refreshtoken.entity.RefreshToken;
import com.aitasker.security.refreshtoken.repository.RefreshTokenRepository;
import com.aitasker.security.ratelimit.LoginAttemptService;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import com.aitasker.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginAttemptService loginAttemptService;
    private final AuditLogService auditLogService;
    private final HttpServletRequest httpServletRequest;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email này đã được sử dụng!");
        }

        User user = new User();
        user.setName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);

        try {
            user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role không hợp lệ. Phải là CLIENT hoặc EXPERT");
        }

        userRepository.save(user);

        String ipAddress = auditLogService.getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        auditLogService.logAction("REGISTER", "USER", user.getId(), user.getId(), 
                user.getEmail(), "User registration", null, ipAddress, userAgent, "SUCCESS");

        return AuthResponse.builder()
                .message("Đăng ký thành công! Vui lòng đăng nhập.")
                .token(null)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String ipAddress = auditLogService.getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");

        if (loginAttemptService.isAccountLocked(request.getEmail())) {
            auditLogService.logLogin(null, request.getEmail(), ipAddress, userAgent, "LOCKED");
            throw new IllegalArgumentException("Tài khoản bị khóa! Vui lòng thử lại sau 15 phút.");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    loginAttemptService.loginFailed(request.getEmail());
                    auditLogService.logLogin(null, request.getEmail(), ipAddress, userAgent, "FAILED_INVALID_EMAIL");
                    return new IllegalArgumentException("Email hoặc mật khẩu không đúng!");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            loginAttemptService.loginFailed(request.getEmail());
            auditLogService.logLogin(user.getId(), user.getEmail(), ipAddress, userAgent, "FAILED_INVALID_PASSWORD");
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng!");
        }

        com.aitasker.security.userdetails.CustomUserDetails userDetails = 
                new com.aitasker.security.userdetails.CustomUserDetails(user);

        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        String rememberMeToken = null;

        if (request.isRememberMe()) {
            rememberMeToken = jwtService.generateRememberMeToken(userDetails);
            user.setRememberMeToken(rememberMeToken);
            user.setRememberMeExpires(LocalDateTime.now().plusDays(30));
        }

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        loginAttemptService.loginSucceeded(request.getEmail());
        auditLogService.logLogin(user.getId(), user.getEmail(), ipAddress, userAgent, "SUCCESS");

        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .rememberMeToken(rememberMeToken)
                .message("Đăng nhập thành công!")
                .build();
    }
}