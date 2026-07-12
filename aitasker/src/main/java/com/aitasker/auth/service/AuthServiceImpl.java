package com.aitasker.auth.service;

import com.aitasker.analytics.enums.AnalyticsEventType;
import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.auth.dto.AuthResponse;
import com.aitasker.auth.dto.LoginRequest;
import com.aitasker.auth.dto.RegisterRequest;
import com.aitasker.auth.entity.PasswordResetToken;
import com.aitasker.auth.repository.PasswordResetTokenRepository;
import com.aitasker.common.enums.Role;
import com.aitasker.common.enums.UserStatus;
import com.aitasker.exception.BadRequestException;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.expert.repository.ExpertProfileRepository;
import com.aitasker.notification.email.EmailService;
import com.aitasker.security.audit.enums.AuditAction;
import com.aitasker.security.audit.service.AuditLogService;
import com.aitasker.security.config.SecurityProperties;
import com.aitasker.security.jwt.JwtService;
import com.aitasker.security.token.RefreshTokenService;
import com.aitasker.security.userdetails.CustomUserDetails;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String LOGIN_FAILED_MESSAGE = "Email hoặc mật khẩu không đúng!";

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ExpertProfileRepository expertProfileRepository;
    private final AnalyticsService analyticsService;
    private final AuditLogService auditLogService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final SecurityProperties securityProperties;

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

        User savedUser = userRepository.save(user);

        if (savedUser.getRole() == Role.EXPERT) {
            com.aitasker.expert.entity.ExpertProfile profile = new com.aitasker.expert.entity.ExpertProfile();
            profile.setUser(savedUser);
            profile.setFullName(request.getFullName());
            expertProfileRepository.save(profile);
        }

        analyticsService.recordEvent(AnalyticsEventType.USER_REGISTERED, savedUser.getId(),
                savedUser.getRole().name(), "User", savedUser.getId().toString());

        return AuthResponse.builder()
                .message("Đăng ký thành công! Vui lòng đăng nhập.")
                .token(null)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            auditLogService.log(AuditAction.LOGIN_FAILED, null, request.getEmail(),
                    "User", null, "Email không tồn tại trong hệ thống");
            throw new IllegalArgumentException(LOGIN_FAILED_MESSAGE);
        }

        assertAccountUsable(user);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            registerFailedAttempt(user);
            throw new IllegalArgumentException(LOGIN_FAILED_MESSAGE);
        }

        // Đăng nhập thành công -> reset bộ đếm Login Attempt Limiting
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        user.setLockedUntil(null);
        user.setLastLogin(LocalDateTime.now());

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = refreshTokenService.issue(user, request.isRememberMe());

        userRepository.save(user);

        analyticsService.recordEvent(AnalyticsEventType.USER_LOGIN, user.getId(),
                user.getRole().name(), "User", user.getId().toString());
        auditLogService.log(AuditAction.LOGIN_SUCCESS, user.getId(), user.getEmail(),
                "User", user.getId().toString(), "Đăng nhập thành công");

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .message("Đăng nhập thành công!")
                .build();
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        User user = refreshTokenService.validate(refreshToken);
        assertAccountUsable(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(refreshToken)
                .message("Làm mới access token thành công!")
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        User user = refreshTokenService.validate(refreshToken);
        refreshTokenService.revoke(user);
        auditLogService.log(AuditAction.LOGOUT, user.getId(), user.getEmail(),
                "User", user.getId().toString(), "Đăng xuất, thu hồi Refresh Token");
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        // Không tiết lộ việc email có tồn tại hay không (tránh User Enumeration) —
        // luôn trả về thành công cho Controller, chỉ thật sự gửi mail khi user tồn tại.
        if (user == null) {
            return;
        }

        String token = generateResetToken();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .used(false)
                .build();
        passwordResetTokenRepository.save(resetToken);

        auditLogService.log(AuditAction.PASSWORD_RESET_REQUESTED, user.getId(), user.getEmail(),
                "User", user.getId().toString(), "Yêu cầu đặt lại mật khẩu");

        emailService.send(user.getEmail(), "Đặt lại mật khẩu AITasker",
                "Mã đặt lại mật khẩu của bạn là: " + token
                        + "\nMã có hiệu lực trong 30 phút. Nếu bạn không yêu cầu, hãy bỏ qua email này.");
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Token đặt lại mật khẩu không hợp lệ."));

        if (!resetToken.isValid()) {
            throw new BadRequestException("Token đặt lại mật khẩu đã hết hạn hoặc đã được sử dụng.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        user.setLockedUntil(null);
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        // Đổi mật khẩu xong -> thu hồi mọi phiên Remember Me/Refresh Token cũ
        refreshTokenService.revoke(user);

        auditLogService.log(AuditAction.PASSWORD_RESET_COMPLETED, user.getId(), user.getEmail(),
                "User", user.getId().toString(), "Đặt lại mật khẩu thành công");

        emailService.send(user.getEmail(), "Mật khẩu của bạn vừa được thay đổi",
                "Mật khẩu tài khoản AITasker của bạn vừa được đặt lại. "
                        + "Nếu không phải bạn thực hiện, hãy liên hệ hỗ trợ ngay.");
    }

    /**
     * Login Attempt Limiting: kiểm tra tài khoản có đang bị khóa tạm thời hoặc bị Admin ban không.
     * Tự động mở khóa nếu thời gian khóa đã trôi qua.
     */
    private void assertAccountUsable(User user) {
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ForbiddenException("Tài khoản đã bị khóa/vô hiệu hóa. Vui lòng liên hệ quản trị viên.");
        }

        if (Boolean.TRUE.equals(user.getAccountLocked())) {
            if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
                throw new ForbiddenException(
                        "Tài khoản tạm thời bị khóa do đăng nhập sai quá nhiều lần. "
                                + "Vui lòng thử lại sau.");
            }
            // Hết thời gian khóa -> tự mở khóa
            user.setAccountLocked(false);
            user.setLockedUntil(null);
            user.setFailedLoginAttempts(0);
        }
    }

    private void registerFailedAttempt(User user) {
        int attempts = user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts();
        attempts++;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= securityProperties.getMaxFailedLoginAttempts()) {
            user.setAccountLocked(true);
            user.setLockedUntil(LocalDateTime.now().plusMinutes(securityProperties.getLockoutDurationMinutes()));
            auditLogService.log(AuditAction.ACCOUNT_LOCKED, user.getId(), user.getEmail(),
                    "User", user.getId().toString(),
                    "Tài khoản bị khóa " + securityProperties.getLockoutDurationMinutes()
                            + " phút sau " + attempts + " lần đăng nhập sai liên tiếp");
        }

        userRepository.save(user);
        auditLogService.log(AuditAction.LOGIN_FAILED, user.getId(), user.getEmail(),
                "User", user.getId().toString(), "Sai mật khẩu (lần " + attempts + ")");
    }

    private String generateResetToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
