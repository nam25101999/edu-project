package com.edu.university.modules.auth.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.auth.annotation.LogAction;
import com.edu.university.modules.auth.entity.AuditLog;
import com.edu.university.modules.auth.repository.AuditLogRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Around("@annotation(logAction)")
    public Object logAround(ProceedingJoinPoint joinPoint, LogAction logAction) throws Throwable {
        long startTime = System.currentTimeMillis();

        HttpServletRequest request = getHttpServletRequest();
        UUID userId = getCurrentUserId(); // Thay vì Username, ta lấy UUID
        Integer status = 200; // Mặc định là HTTP 200 SUCCESS
        String errorMessage = null;
        String responsePayload = null;
        Object result = null;

        try {
            // Chạy API
            result = joinPoint.proceed();

            // Cập nhật status code thực tế từ Response
            HttpServletResponse response = getHttpServletResponse();
            if (response != null) {
                status = response.getStatus();
            }

            responsePayload = safeJson(result);
            return result;

        } catch (Exception e) {
            status = 500; // Nếu xảy ra lỗi (Exception), gán HTTP 500
            errorMessage = e.getMessage();
            throw e;

        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            saveAuditLog(joinPoint, logAction, request, userId, status, errorMessage, responsePayload, executionTime);
        }
    }

    private void saveAuditLog(ProceedingJoinPoint joinPoint, LogAction logAction, HttpServletRequest request,
                              UUID userId, Integer status, String errorMessage, String responsePayload, long executionTime) {
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

            List<Object> safeArgs = Arrays.stream(joinPoint.getArgs())
                    .filter(arg -> !(arg instanceof HttpServletRequest)
                            && !(arg instanceof HttpServletResponse)
                            && !(arg instanceof MultipartFile))
                    .toList();
            String requestPayload = safeJson(safeArgs);

            // Xử lý an toàn Enum Action từ Annotation
            AuditLog.AuditAction actionEnum;
            try {
                actionEnum = AuditLog.AuditAction.valueOf(logAction.action().toUpperCase());
            } catch (Exception e) {
                actionEnum = AuditLog.AuditAction.UPDATE; // Fallback an toàn nếu nhập sai Action string
            }

            AuditLog auditLog = AuditLog.builder()
                    .userId(userId) // Sử dụng userId UUID
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .action(actionEnum) // Truyền vào Enum
                    .entityName(logAction.entityName())
                    .httpMethod(httpMethod)
                    .endpoint(endpoint)
                    .requestPayload(requestPayload)
                    .responsePayload(responsePayload)
                    .executionTimeMs(executionTime)
                    .status(status) // Truyền vào HTTP status code
                    .errorMessage(errorMessage)
                    .build();

            auditLogRepository.save(auditLog);

        } catch (Exception e) {
            log.error("Lỗi khi lưu AuditLog (Không làm sập hệ thống chính): {}", e.getMessage());
        }
    }

    // Hàm lấy ID (UUID) của User hiện tại thay vì Username
    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        return null; // Guest / Unauthenticated
    }

    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    // Bổ sung hàm lấy Response để đọc Http Status Code
    private HttpServletResponse getHttpServletResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getResponse() : null;
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