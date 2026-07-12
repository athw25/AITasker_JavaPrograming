package com.aitasker.auth.service;

import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.auth.dto.LoginRequest;
import com.aitasker.auth.repository.PasswordResetTokenRepository;
import com.aitasker.common.enums.Role;
import com.aitasker.common.enums.UserStatus;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.expert.repository.ExpertProfileRepository;
import com.aitasker.notification.email.EmailService;
import com.aitasker.security.audit.service.AuditLogService;
import com.aitasker.security.config.SecurityProperties;
import com.aitasker.security.jwt.JwtService;
import com.aitasker.security.token.RefreshTokenService;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;
    @Mock private ExpertProfileRepository expertProfileRepository;
    @Mock private AnalyticsService analyticsService;
    @Mock private AuditLogService auditLogService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private SecurityProperties securityProperties;
    private User user;

    @BeforeEach
    void setUp() {
        securityProperties = new SecurityProperties();
        securityProperties.setMaxFailedLoginAttempts(3);
        securityProperties.setLockoutDurationMinutes(15);
        // Do @InjectMocks không tự inject field không phải @Mock, gán thủ công
        org.springframework.test.util.ReflectionTestUtils.setField(
                authService, "securityProperties", securityProperties);

        user = new User();
        user.setId(1L);
        user.setEmail("user@aitasker.com");
        user.setPassword("hashed-password");
        user.setRole(Role.CLIENT);
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
    }

    @Test
    void login_saiMatKhau_tangBoDemVaKhongKhoaNeuChuaVuotNguong() {
        LoginRequest request = new LoginRequest();
        request.setEmail(user.getEmail());
        request.setPassword("wrong-password");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(user.getFailedLoginAttempts()).isEqualTo(1);
        assertThat(user.getAccountLocked()).isFalse();
    }

    @Test
    void login_saiMatKhauVuotNguong_khoaTaiKhoan() {
        user.setFailedLoginAttempts(2); // lần này là lần sai thứ 3 -> đạt ngưỡng maxFailedLoginAttempts=3

        LoginRequest request = new LoginRequest();
        request.setEmail(user.getEmail());
        request.setPassword("wrong-password");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(user.getFailedLoginAttempts()).isEqualTo(3);
        assertThat(user.getAccountLocked()).isTrue();
        assertThat(user.getLockedUntil()).isAfter(LocalDateTime.now());
    }

    @Test
    void login_taiKhoanDangBiKhoa_biTuChoiDuMatKhauDung() {
        user.setAccountLocked(true);
        user.setLockedUntil(LocalDateTime.now().plusMinutes(10));

        LoginRequest request = new LoginRequest();
        request.setEmail(user.getEmail());
        request.setPassword("correct-password");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ForbiddenException.class);

        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void login_taiKhoanBiBan_biTuChoi() {
        user.setStatus(UserStatus.BANNED);

        LoginRequest request = new LoginRequest();
        request.setEmail(user.getEmail());
        request.setPassword("correct-password");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void login_thanhCong_resetBoDemVaCapRefreshToken() {
        user.setFailedLoginAttempts(2);

        LoginRequest request = new LoginRequest();
        request.setEmail(user.getEmail());
        request.setPassword("correct-password");
        request.setRememberMe(true);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correct-password", user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("fake-jwt");
        when(refreshTokenService.issue(user, true)).thenReturn("fake-refresh-token");

        var response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("fake-jwt");
        assertThat(response.getRefreshToken()).isEqualTo("fake-refresh-token");
        assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
        assertThat(user.getAccountLocked()).isFalse();
        verify(refreshTokenService).issue(user, true);
    }
}
