package com.edu.university.aspect;

import com.edu.university.annotation.LogAction;
import com.edu.university.entity.AuditLog;
import com.edu.university.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;

    // =========================
    // SUCCESS LOG
    // =========================
    @AfterReturning(value = "@annotation(logAction)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, LogAction logAction, Object result) {

        try {
            saveLog(joinPoint, logAction, "SUCCESS", null);
        } catch (Exception e) {
            // tránh crash app nếu log lỗi
            e.printStackTrace();
        }
    }

    // =========================
    // ERROR LOG
    // =========================
    @AfterThrowing(value = "@annotation(logAction)", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, LogAction logAction, Exception ex) {

        try {
            saveLog(joinPoint, logAction, "ERROR", ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // CORE LOGIC
    // =========================
    private void saveLog(JoinPoint joinPoint, LogAction logAction, String status, String errorMessage) {

        // 1. USER
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "SYSTEM";

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            username = authentication.getName();
        }

        // 2. REQUEST
        String ipAddress = "Unknown";
        String url = "N/A";

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            url = request.getRequestURI();
            ipAddress = request.getRemoteAddr();

            String forwardedFor = request.getHeader("X-Forwarded-For");
            if (forwardedFor != null && !forwardedFor.isEmpty()) {
                ipAddress = forwardedFor.split(",")[0];
            }
        }

        // 3. METHOD
        String methodName = joinPoint.getSignature().getName();

        // 4. PARAMS (Ẩn password)
        String details = Arrays.stream(joinPoint.getArgs())
                .map(arg -> {
                    if (arg == null) return "null";

                    String str = arg.toString().toLowerCase();
                    if (str.contains("password")) {
                        return "****";
                    }

                    return arg.toString();
                })
                .toList()
                .toString();

        if (details.length() > 500) {
            details = details.substring(0, 497) + "...";
        }

        // 5. ERROR
        if (errorMessage != null) {
            details += " | ERROR: " + errorMessage;
        }

        // 6. SAVE
        AuditLog auditLog = AuditLog.builder()
                .username(username)
                .action(logAction.action())
                .entityName(logAction.entityName())
                .details(details)
                .ipAddress(ipAddress)
                .createdAt(LocalDateTime.now())
                // nếu entity có thêm field thì nên thêm:
                // .status(status)
                // .method(methodName)
                // .url(url)
                .build();

        auditLogRepository.save(auditLog);
    }
}