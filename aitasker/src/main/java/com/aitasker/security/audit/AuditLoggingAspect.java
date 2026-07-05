package com.aitasker.security.audit;

import com.aitasker.audit.service.AuditLogService;
import com.aitasker.common.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLoggingAspect {

    private final AuditLogService auditLogService;
    private final HttpServletRequest httpServletRequest;

    @Before("@annotation(auditable)")
    public void logMethodExecution(JoinPoint joinPoint, Auditable auditable) {
        String ipAddress = auditLogService.getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        Long userId = SecurityUtils.getCurrentUserId();
        String userEmail = SecurityUtils.getCurrentUserEmail();

        auditLogService.logAction(auditable.action(), auditable.entityType(),
                null, userId, userEmail, auditable.description(),
                null, ipAddress, userAgent, "SUCCESS");

        log.info("Audit logged for: {} - {}", auditable.action(), auditable.entityType());
    }

    @AfterThrowing(pointcut = "@annotation(auditable)", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Auditable auditable, Exception exception) {
        String ipAddress = auditLogService.getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        Long userId = SecurityUtils.getCurrentUserId();
        String userEmail = SecurityUtils.getCurrentUserEmail();

        auditLogService.logAction(auditable.action(), auditable.entityType(),
                null, userId, userEmail, auditable.description(),
                exception.getMessage(), ipAddress, userAgent, "FAILED");

        log.error("Audit logged with error for: {} - {}", auditable.action(), auditable.entityType(), exception);
    }
}

