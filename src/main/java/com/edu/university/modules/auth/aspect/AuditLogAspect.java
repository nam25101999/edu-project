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
        UUID userId = getCurrentUserId(); // Thay vÃ¬ Username, ta láº¥y UUID
        Integer status = 200; // Máº·c Ä‘á»‹nh lÃ  HTTP 200 SUCCESS
        String errorMessage = null;
        String responsePayload = null;
        Object result = null;

        try {
            // Cháº¡y API
            result = joinPoint.proceed();

            // Cáº­p nháº­t status code thá»±c táº¿ tá»« Response
            HttpServletResponse response = getHttpServletResponse();
            if (response != null) {
                status = response.getStatus();
            }

            responsePayload = safeJson(result);
            return result;

        } catch (Exception e) {
            status = 500; // Náº¿u xáº£y ra lá»—i (Exception), gÃ¡n HTTP 500
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

            // Xá»­ lÃ½ an toÃ n Enum Action tá»« Annotation
            AuditLog.AuditAction actionEnum;
            try {
                actionEnum = AuditLog.AuditAction.valueOf(logAction.action().toUpperCase());
            } catch (Exception e) {
                actionEnum = AuditLog.AuditAction.UPDATE; // Fallback an toÃ n náº¿u nháº­p sai Action string
            }

            AuditLog auditLog = AuditLog.builder()
                    .userId(userId) // Sá»­ dá»¥ng userId UUID
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .action(actionEnum) // Truyá»n vÃ o Enum
                    .entityName(logAction.entityName())
                    .httpMethod(httpMethod)
                    .endpoint(endpoint)
                    .requestPayload(requestPayload)
                    .responsePayload(responsePayload)
                    .executionTimeMs(executionTime)
                    .status(status) // Truyá»n vÃ o HTTP status code
                    .errorMessage(errorMessage)
                    .build();

            auditLogRepository.save(auditLog);

        } catch (Exception e) {
            log.error("Lá»—i khi lÆ°u AuditLog (KhÃ´ng lÃ m sáº­p há»‡ thá»‘ng chÃ­nh): {}", e.getMessage());
        }
    }

    // HÃ m láº¥y ID (UUID) cá»§a User hiá»‡n táº¡i thay vÃ¬ Username
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

    // Bá»• sung hÃ m láº¥y Response Ä‘á»ƒ Ä‘á»c Http Status Code
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
