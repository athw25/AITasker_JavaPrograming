package com.aitasker.security.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private record RuleMatch(String prefix, String method) {
        boolean matches(HttpServletRequest request) {
            return request.getRequestURI().startsWith(prefix) && method.equalsIgnoreCase(request.getMethod());
        }
    }

    private static final List<RuleMatch> STRICT_RULES = List.of(
            new RuleMatch("/api/auth/login", "POST"),
            new RuleMatch("/api/auth/register", "POST")
    );

    private static final List<RuleMatch> AI_RULES = List.of(
            new RuleMatch("/api/ai/", "POST"),
            new RuleMatch("/api/ai/", "GET")
    );

    @Value("${app.ratelimit.auth.max-attempts:5}")
    private int authMaxAttempts;

    @Value("${app.ratelimit.auth.window-ms:60000}")
    private long authWindowMs;

    @Value("${app.ratelimit.ai.max-attempts:20}")
    private int aiMaxAttempts;

    @Value("${app.ratelimit.ai.window-ms:60000}")
    private long aiWindowMs;

    private final ConcurrentHashMap<String, Bucket> authBuckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Bucket> aiBuckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String key = clientIp(request);

        if (STRICT_RULES.stream().anyMatch(rule -> rule.matches(request))) {
            if (isRateLimited(authBuckets, key, authMaxAttempts, authWindowMs)) {
                reject(response, "Quá nhiều yêu cầu đăng nhập/đăng ký, vui lòng thử lại sau.");
                return;
            }
        } else if (AI_RULES.stream().anyMatch(rule -> rule.matches(request))) {
            if (isRateLimited(aiBuckets, key, aiMaxAttempts, aiWindowMs)) {
                reject(response, "Quá nhiều yêu cầu tới AI Module, vui lòng thử lại sau.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isRateLimited(ConcurrentHashMap<String, Bucket> buckets, String key, int maxAttempts, long windowMs) {
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket());

        long now = System.currentTimeMillis();
        if (now - bucket.windowStart.get() > windowMs) {
            bucket.windowStart.set(now);
            bucket.count.set(0);
        }

        return bucket.count.incrementAndGet() > maxAttempts;
    }

    private void reject(HttpServletResponse response, String message) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json");
        response.getWriter().write("{\"success\":false,\"message\":\"" + message + "\"}");
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return forwarded != null ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
    }

    private static class Bucket {
        private final AtomicInteger count = new AtomicInteger(0);
        private final AtomicLong windowStart = new AtomicLong(System.currentTimeMillis());
    }
}
