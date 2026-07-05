package com.aitasker.security.audit;

import java.lang.annotation.*;

/**
 * Annotation để mark một method cần audit logging
 * Sử dụng cùng với AuditLoggingAspect để automatic log actions
 * 
 * Ví dụ:
 * @Auditable(action = "LOGIN", entityType = "User")
 * public AuthResponse login(LoginRequest req) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {
    String action() default "";
    String entityType() default "";
    String description() default "";
}
