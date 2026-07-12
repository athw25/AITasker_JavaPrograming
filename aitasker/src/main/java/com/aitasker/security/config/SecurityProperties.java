package com.aitasker.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.security")
@Getter
@Setter
public class SecurityProperties {

    /** Số lần đăng nhập sai liên tiếp tối đa trước khi khóa tài khoản tạm thời. */
    private int maxFailedLoginAttempts = 5;

    /** Thời gian khóa tài khoản (phút) sau khi vượt quá số lần đăng nhập sai. */
    private int lockoutDurationMinutes = 15;

    /** Thời hạn Refresh Token khi KHÔNG bật Remember Me (ngày). */
    private int refreshTokenExpirationDays = 1;

    /** Thời hạn Refresh Token khi CÓ bật Remember Me (ngày). */
    private int rememberMeExpirationDays = 30;

    /** Số request tối đa cho mỗi IP trong một cửa sổ thời gian, áp dụng cho các endpoint nhạy cảm (login/register/forgot-password). */
    private int rateLimitMaxRequests = 10;

    /** Độ dài cửa sổ thời gian của Rate Limiting (giây). */
    private int rateLimitWindowSeconds = 60;
}
