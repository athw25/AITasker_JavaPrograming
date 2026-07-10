package com.aitasker.auth.service;

import com.aitasker.auth.dto.AuthResponse;
import com.aitasker.auth.dto.LoginRequest;
import com.aitasker.auth.dto.RegisterRequest;
import com.aitasker.common.enums.Role;
import com.aitasker.expert.repository.ExpertProfileRepository;
import com.aitasker.security.jwt.JwtService;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.aitasker.audit.service.AuditLogService;
import com.aitasker.security.refreshtoken.entity.RefreshToken;
import com.aitasker.security.refreshtoken.repository.RefreshTokenRepository;
import com.aitasker.security.ratelimit.LoginAttemptService;
import java.time.Instant;


import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ExpertProfileRepository expertProfileRepository;
    private final AuditLogService auditLogService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginAttemptService loginAttemptService;



    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email này đã được sử dụng!");
        }

        User user = new User();
        user.setName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role requestedRole;
        try {
            requestedRole = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role không hợp lệ. Phải là CLIENT hoặc EXPERT");
        }

        // Trước đây Role.valueOf(...) cho qua cả "ADMIN" vì Role enum có
        // giá trị đó -> bất kỳ ai cũng tự đăng ký được tài khoản ADMIN qua
        // API public. Chặn tường minh: API đăng ký công khai chỉ tạo được
        // CLIENT hoặc EXPERT. Tài khoản ADMIN phải được tạo bằng cách khác
        // (seed dữ liệu, migration, hoặc một API admin-only riêng).
        if (requestedRole != Role.CLIENT && requestedRole != Role.EXPERT) {
            throw new IllegalArgumentException("Role không hợp lệ. Phải là CLIENT hoặc EXPERT");
        }

        user.setRole(requestedRole);

        userRepository.save(user);

        if (user.getRole() == Role.EXPERT) {
            com.aitasker.expert.entity.ExpertProfile profile = new com.aitasker.expert.entity.ExpertProfile();
            profile.setUser(user);
            profile.setFullName(request.getFullName());
            expertProfileRepository.save(profile);
        }

        return AuthResponse.builder()
                .message("Đăng ký thành công! Vui lòng đăng nhập.")
                .token(null)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String ipAddress = "UNKNOWN";
        if (attributes != null) {
            HttpServletRequest req = attributes.getRequest();
            String xff = req.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isEmpty()) {
                ipAddress = xff.split(",")[0].trim();
            } else {
                ipAddress = req.getRemoteAddr();
            }
        }

        // 1. Kiểm tra khóa đăng nhập do brute force
        if (loginAttemptService.isBlocked(request.getEmail())) {
            throw new IllegalArgumentException("Tài khoản này đang bị khóa tạm thời do đăng nhập sai nhiều lần. Vui lòng quay lại sau 15 phút!");
        }
        if (loginAttemptService.isBlocked(ipAddress)) {
            throw new IllegalArgumentException("Địa chỉ IP của bạn đang bị khóa tạm thời do đăng nhập sai nhiều lần. Vui lòng quay lại sau 15 phút!");
        }

        com.aitasker.user.entity.User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            loginAttemptService.loginFailed(request.getEmail());
            loginAttemptService.loginFailed(ipAddress);
            auditLogService.log("LOGIN_FAILED", "Email không tồn tại: " + request.getEmail(), request.getEmail(), ipAddress);
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng!");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            loginAttemptService.loginFailed(request.getEmail());
            loginAttemptService.loginFailed(ipAddress);
            auditLogService.log("LOGIN_FAILED", "Sai mật khẩu", request.getEmail(), ipAddress);
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng!");
        }

        // Đăng nhập thành công -> Reset đếm lỗi
        loginAttemptService.loginSucceeded(request.getEmail());
        loginAttemptService.loginSucceeded(ipAddress);

        com.aitasker.security.userdetails.CustomUserDetails userDetails = new com.aitasker.security.userdetails.CustomUserDetails(user);
        String jwtToken = jwtService.generateToken(userDetails);

        // Tạo & Lưu Refresh Token
        boolean rememberMe = request.getRememberMe() != null && request.getRememberMe();
        String refreshToken = createAndSaveRefreshToken(user, rememberMe);

        auditLogService.log("LOGIN_SUCCESS", "Đăng nhập thành công", user.getEmail(), ipAddress);

        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .message("Đăng nhập thành công!")
                .build();
    }

    @Override
    public AuthResponse refresh(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token không hợp lệ hoặc không tồn tại!"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("Refresh token đã hết hạn! Vui lòng đăng nhập lại.");
        }

        com.aitasker.user.entity.User user = refreshToken.getUser();
        com.aitasker.security.userdetails.CustomUserDetails userDetails = new com.aitasker.security.userdetails.CustomUserDetails(user);
        String newAccessToken = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(refreshTokenStr)
                .message("Lấy token mới thành công!")
                .build();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void logout(String refreshTokenStr) {
        refreshTokenRepository.deleteByToken(refreshTokenStr);
    }

    private String createAndSaveRefreshToken(com.aitasker.user.entity.User user, boolean rememberMe) {
        // Thu hồi token cũ nếu có
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);

        // rememberMe = 30 ngày, ngược lại = 24 giờ
        long expirationMs = rememberMe ? 2592000000L : 86400000L;
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(java.util.UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(expirationMs))
                .build();

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }
}