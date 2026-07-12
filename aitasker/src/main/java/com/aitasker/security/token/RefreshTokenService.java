package com.aitasker.security.token;

import com.aitasker.exception.UnauthorizedException;
import com.aitasker.security.config.SecurityProperties;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Refresh Token dạng opaque (chuỗi ngẫu nhiên, không phải JWT), lưu phía Server
 * trên chính bản ghi User — cho phép thu hồi ngay lập tức (logout) mà không cần
 * chờ token hết hạn, khác với JWT access token vốn không thể thu hồi giữa chừng.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final SecurityProperties securityProperties;

    @Transactional
    public String issue(User user, boolean rememberMe) {
        String token = generateOpaqueToken();
        int expirationDays = rememberMe
                ? securityProperties.getRememberMeExpirationDays()
                : securityProperties.getRefreshTokenExpirationDays();

        user.setRememberMeToken(token);
        user.setRememberMeExpires(LocalDateTime.now().plusDays(expirationDays));
        userRepository.save(user);
        return token;
    }

    @Transactional(readOnly = true)
    public User validate(String refreshToken) {
        User user = userRepository.findByRememberMeToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Refresh token không hợp lệ."));

        if (user.getRememberMeExpires() == null || user.getRememberMeExpires().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token đã hết hạn, vui lòng đăng nhập lại.");
        }
        return user;
    }

    @Transactional
    public void revoke(User user) {
        user.setRememberMeToken(null);
        user.setRememberMeExpires(null);
        userRepository.save(user);
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[48];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
