package com.aitasker.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private static final long WINDOW_MILLIS = 60_000;
    private final ConcurrentHashMap<String, RequestCounter> limitCache = new ConcurrentHashMap<>();

    // Chỉ tin header X-Forwarded-For khi ứng dụng thực sự chạy sau reverse proxy đáng tin cậy
    // (Nginx/Load Balancer); nếu không, client có thể tự set header này để giả IP và né rate limit.
    @Value("${app.security.trust-forwarded-header:false}")
    private boolean trustForwardedHeader;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = getClientIp(request);
        long currentTimeMillis = System.currentTimeMillis();

        RequestCounter counter = limitCache.compute(ip, (k, v) -> {
            if (v == null || currentTimeMillis - v.windowStartTime > WINDOW_MILLIS) {
                return new RequestCounter(currentTimeMillis, new AtomicInteger(1));
            } else {
                v.count.incrementAndGet();
                return v;
            }
        });

        if (counter.count.get() > MAX_REQUESTS_PER_MINUTE) {
            log.warn("Rate limit exceeded for IP: {}", ip);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"Too many requests. Please try again after a minute.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        if (trustForwardedHeader) {
            String xff = request.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isEmpty()) {
                return xff.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    // Dọn các entry đã hết hạn window để tránh limitCache phình to vô hạn khi server chạy lâu dài
    @Scheduled(fixedRate = 5 * WINDOW_MILLIS)
    void cleanupExpiredEntries() {
        long now = System.currentTimeMillis();
        limitCache.entrySet().removeIf(entry -> now - entry.getValue().windowStartTime > WINDOW_MILLIS);
    }

    private static class RequestCounter {
        long windowStartTime;
        AtomicInteger count;

        RequestCounter(long windowStartTime, AtomicInteger count) {
            this.windowStartTime = windowStartTime;
            this.count = count;
        }
    }
}
