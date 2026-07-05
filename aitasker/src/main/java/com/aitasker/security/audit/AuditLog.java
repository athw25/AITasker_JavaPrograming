package com.aitasker.security.audit;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {
    String actionType() default "";
    String entityType() default "";
    String description() default "";
}
