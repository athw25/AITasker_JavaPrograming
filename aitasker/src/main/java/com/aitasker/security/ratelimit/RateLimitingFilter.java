package com.aitasker.security.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/api/auth/login";

    @Value("${app.ratelimit.login.max-attempts:5}")
    private int maxAttempts;

    @Value("${app.ratelimit.login.window-ms:60000}")
    private long windowMs;

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (LOGIN_PATH.equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod())) {
            String key = clientIp(request);
            Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket());

            long now = System.currentTimeMillis();
            if (now - bucket.windowStart.get() > windowMs) {
                bucket.windowStart.set(now);
                bucket.count.set(0);
            }

            if (bucket.count.incrementAndGet() > maxAttempts) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"success\":false,\"message\":\"Quá nhiều lần thử đăng nhập, vui lòng thử lại sau.\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return forwarded != null ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
    }

    private static class Bucket {
        private final AtomicInteger count = new AtomicInteger(0);
        private final java.util.concurrent.atomic.AtomicLong windowStart =
                new java.util.concurrent.atomic.AtomicLong(System.currentTimeMillis());
    }
}
