package com.edu.university.modules.report.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.edu.university.modules.report.annotation.LogAction;
import com.edu.university.modules.report.AuditLog;
import com.edu.university.modules.report.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper; // Dùng để parse JSON an toàn

    /**
     * Dùng @Around để "ôm" trọn hàm, cho phép đo thời gian và bắt lỗi Exception
     */
    @Around("@annotation(logAction)")
    public Object logAround(ProceedingJoinPoint joinPoint, LogAction logAction) throws Throwable {
        long startTime = System.currentTimeMillis();

        HttpServletRequest request = getHttpServletRequest();
        String username = getCurrentUsername();
        String status = "SUCCESS";
        String errorMessage = null;
        String responsePayload = null;
        Object result = null;

        try {
            // Chạy API
            result = joinPoint.proceed();

            // ✅ FIX: xử lý response đúng cách
            responsePayload = safeJson(result);

            return result;

        } catch (Exception e) {
            status = "FAILED";
            errorMessage = e.getMessage();
            throw e;

        } finally {
            long executionTime = System.currentTimeMillis() - startTime;

            saveAuditLog(joinPoint, logAction, request, username, status, errorMessage, responsePayload, executionTime);
        }
    }

    private void saveAuditLog(ProceedingJoinPoint joinPoint, LogAction logAction, HttpServletRequest request,
                              String username, String status, String errorMessage, String responsePayload, long executionTime) {
        try {
            String ipAddress = "Unknown";
            String httpMethod = "Unknown";
            String endpoint = "Unknown";
            String userAgent = "Unknown";

            if (request != null) {
                ipAddress = request.getHeader("X-Forwarded-For");
                if (ipAddress == null || ipAddress.isEmpty()) {
                    ipAddress = request.getRemoteAddr();
                }
                httpMethod = request.getMethod();
                endpoint = request.getRequestURI();
                userAgent = request.getHeader("User-Agent");
            }

            // Lấy dữ liệu người dùng gửi lên (Loại bỏ các object không thể serialize như File, Request)
            List<Object> safeArgs = Arrays.stream(joinPoint.getArgs())
                    .filter(arg -> !(arg instanceof HttpServletRequest)
                            && !(arg instanceof HttpServletResponse)
                            && !(arg instanceof MultipartFile))
                    .toList();
            String requestPayload = safeJson(safeArgs);

            AuditLog auditLog = AuditLog.builder()
                    .username(username)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .action(logAction.action())
                    .entityName(logAction.entityName())
                    .httpMethod(httpMethod)
                    .endpoint(endpoint)
                    .requestPayload(requestPayload)
                    .responsePayload(responsePayload)
                    .executionTimeMs(executionTime)
                    .status(status)
                    .errorMessage(errorMessage)
                    .createdAt(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);

        } catch (Exception e) {
            log.error("Lỗi khi lưu AuditLog (Không làm sập hệ thống chính): {}", e.getMessage());
        }
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return auth.getName();
        }
        return "SYSTEM";
    }

    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String serializeToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return "Cannot serialize to JSON";
        }
    }
    private String maskSensitiveData(String payload) {
        if (payload == null) return null;

        return payload
                .replaceAll("(?i)\"password\"\\s*:\\s*\".*?\"", "\"password\":\"***\"")
                .replaceAll("(?i)\"token\"\\s*:\\s*\".*?\"", "\"token\":\"***\"")
                .replaceAll("(?i)\"accessToken\"\\s*:\\s*\".*?\"", "\"accessToken\":\"***\"");
    }
    private String safeJson(Object data) {
        String json = serializeToJson(data);

        json = maskSensitiveData(json);

        if (json != null && json.length() > 2000) {
            json = json.substring(0, 1997) + "...";
        }

        return json;
    }
}