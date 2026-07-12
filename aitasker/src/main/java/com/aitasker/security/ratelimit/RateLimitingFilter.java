package com.aitasker.security.ratelimit;

import com.aitasker.security.config.SecurityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate Limiting đơn giản dựa trên bộ nhớ (sliding window theo cửa sổ cố định), áp dụng
 * cho các endpoint public dễ bị brute-force/spam: login, register, forgot-password.
 * Lưu ý: đây là giải pháp in-memory, phù hợp một instance; khi scale nhiều instance
 * cần chuyển sang Redis (Bucket4j + Redis) — nằm ngoài phạm vi đồ án hiện tại.
 */
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final List<String> PROTECTED_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/forgot-password"
    );

    private final SecurityProperties securityProperties;
    private final ObjectMapper objectMapper;

    private final Map<String, Window> windowsByKey = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PROTECTED_PATHS.stream().noneMatch(path -> request.getRequestURI().endsWith(path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String key = request.getRequestURI() + "|" + clientIp(request);
        Window window = windowsByKey.computeIfAbsent(key, k -> new Window());

        if (window.exceeds(securityProperties.getRateLimitMaxRequests(),
                securityProperties.getRateLimitWindowSeconds())) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                    "success", false,
                    "message", "Quá nhiều yêu cầu, vui lòng thử lại sau ít phút."
            )));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /** Cửa sổ đếm request cố định, tự reset khi hết hạn cửa sổ. */
    private static class Window {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile long windowStartEpochSecond = Instant.now().getEpochSecond();

        synchronized boolean exceeds(int maxRequests, int windowSeconds) {
            long now = Instant.now().getEpochSecond();
            if (now - windowStartEpochSecond >= windowSeconds) {
                windowStartEpochSecond = now;
                count.set(0);
            }
            return count.incrementAndGet() > maxRequests;
        }
    }
}
