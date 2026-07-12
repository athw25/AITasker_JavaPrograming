package com.aitasker.auth.service;

import com.aitasker.audit.service.AuditLogService;
import com.aitasker.auth.dto.AuthResponse;
import com.aitasker.auth.dto.LoginRequest;
import com.aitasker.auth.dto.RegisterRequest;
import com.aitasker.auth.entity.PasswordResetToken;
import com.aitasker.auth.repository.PasswordResetTokenRepository;
import com.aitasker.common.enums.Role;
import com.aitasker.email.service.EmailService;
import com.aitasker.exception.BadRequestException;
import com.aitasker.expert.repository.ExpertProfileRepository;
import com.aitasker.security.jwt.JwtService;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ExpertProfileRepository expertProfileRepository;
    private final AuditLogService auditLogService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Value("${app.password-reset.expiration-ms:1800000}")
    private long resetExpirationMs;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

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
        // 1. Tìm user trong Database dựa vào email
        com.aitasker.user.entity.User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email hoặc mật khẩu không đúng!"));

        // 2. Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng!");
        }

        if (user.getStatus() != com.aitasker.common.enums.UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Tài khoản đã bị khóa hoặc vô hiệu hóa!");
        }

        // Gói Entity User (của TV2) vào trong CustomUserDetails (của TV3)
        com.aitasker.security.userdetails.CustomUserDetails userDetails = new com.aitasker.security.userdetails.CustomUserDetails(user);

        // 4. Sinh JWT Token
        String jwtToken = jwtService.generateToken(userDetails);
        com.aitasker.auth.entity.RefreshToken refreshToken = refreshTokenService.create(user);

        auditLogService.log(user, "LOGIN", "Đăng nhập thành công");

        // 5. Trả token về cho Client
        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken.getToken())
                .message("Đăng nhập thành công!")
                .build();
    }

    @Override
    public AuthResponse refresh(String refreshTokenValue) {
        com.aitasker.auth.entity.RefreshToken refreshToken = refreshTokenService.verify(refreshTokenValue);
        User user = refreshToken.getUser();

        com.aitasker.security.userdetails.CustomUserDetails userDetails =
                new com.aitasker.security.userdetails.CustomUserDetails(user);
        String jwtToken = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken.getToken())
                .message("Làm mới token thành công!")
                .build();
    }

    @Override
    public void logout(String refreshTokenValue) {
        refreshTokenService.revoke(refreshTokenValue);
    }

    @Override
    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(UUID.randomUUID().toString())
                    .user(user)
                    .expiryDate(LocalDateTime.now().plusNanos(resetExpirationMs * 1_000_000))
                    .used(false)
                    .build();
            passwordResetTokenRepository.save(resetToken);

            String resetLink = frontendUrl + "/reset-password?token=" + resetToken.getToken();
            emailService.send(user.getEmail(), "Đặt lại mật khẩu AITasker",
                    "Nhấn vào link sau để đặt lại mật khẩu (hết hạn sau 30 phút): " + resetLink);

            auditLogService.log(user, "FORGOT_PASSWORD_REQUESTED", "Yêu cầu đặt lại mật khẩu");
        });
        // Không tiết lộ email có tồn tại hay không — luôn trả về thành công như nhau.
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Token đặt lại mật khẩu không hợp lệ"));

        if (resetToken.isUsed() || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token đã hết hạn hoặc đã được sử dụng");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        refreshTokenService.revokeAllForUser(user.getId());
        auditLogService.log(user, "PASSWORD_RESET", "Đặt lại mật khẩu thành công");
    }

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.scheduling.annotation.Scheduled(cron = "0 30 3 * * *")
    public void cleanupExpiredPasswordResetTokens() {
        passwordResetTokenRepository.deleteExpiredOrUsed(LocalDateTime.now());
    }
}