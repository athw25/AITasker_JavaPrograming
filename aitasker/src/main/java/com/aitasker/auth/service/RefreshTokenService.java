package com.aitasker.auth.service;

import com.aitasker.auth.entity.RefreshToken;
import com.aitasker.auth.repository.RefreshTokenRepository;
import com.aitasker.exception.BadRequestException;
import com.aitasker.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    public RefreshToken create(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(LocalDateTime.now().plusNanos(refreshExpirationMs * 1_000_000))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verify(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Refresh token không hợp lệ"));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Refresh token đã hết hạn hoặc bị thu hồi");
        }
        return refreshToken;
    }

    public void revoke(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    public void revokeAllForUser(Long userId) {
        refreshTokenRepository.deleteByUser_Id(userId);
    }

    @Transactional
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupExpiredTokens() {
        int deleted = refreshTokenRepository.deleteExpiredOrRevoked(LocalDateTime.now());
        if (deleted > 0) {
            log.info("Đã dọn {} refresh token hết hạn/đã thu hồi", deleted);
        }
    }
}
