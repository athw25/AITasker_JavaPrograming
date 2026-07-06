package com.aitasker.common.security;

import com.aitasker.user.entity.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {
        throw new AssertionError();
    }

    /**
     * Lấy ID của user hiện tại từ SecurityContext
     * @return userId hoặc null nếu không có user đang login
     */
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        return null;
    }

    /**
     * Lấy email của user hiện tại từ SecurityContext
     * @return email hoặc null nếu không có user đang login
     */
    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser().getEmail();
        }
        return null;
    }

    /**
     * Lấy username của user hiện tại từ SecurityContext
     * @return username hoặc null nếu không có user đang login
     */
    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getName();
        }
        return null;
    }

    /**
     * Check xem user hiện tại có phải admin không
     * @return true nếu là admin
     */
    public static boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }

    /**
     * Lấy IP address từ current request
     * @return IP address
     */
    public static String getCurrentRequestIp() {
        try {
            org.springframework.web.context.request.RequestAttributes attrs =
                    org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attrs instanceof org.springframework.web.context.request.ServletRequestAttributes servletAttrs) {
                return servletAttrs.getRequest().getRemoteAddr();
            }
        } catch (Exception e) {
            // Ignore
        }
        return "unknown";
    }
}

