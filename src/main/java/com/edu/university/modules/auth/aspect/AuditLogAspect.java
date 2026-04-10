package com.edu.university.modules.auth.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.edu.university.common.exception.BusinessException;
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
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    public static final String AUDIT_LOGGED_ATTRIBUTE = AuditLogAspect.class.getName() + ".AUDIT_LOGGED";
    private static final Pattern CAMEL_CASE_BOUNDARY = Pattern.compile("(?<!^)([A-Z])");

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Around("execution(public * com.edu.university.modules..controller..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        HttpServletRequest request = getHttpServletRequest();
        UUID userId = getCurrentUserId();
        LogAction logAction = resolveLogAction(joinPoint);
        Integer status = 200;
        String errorMessage = null;
        String responsePayload = null;
        Object result = null;

        try {
            result = joinPoint.proceed();

            HttpServletResponse response = getHttpServletResponse();
            if (response != null) {
                status = response.getStatus();
            }
            if (result instanceof ResponseEntity<?> responseEntity) {
                status = responseEntity.getStatusCode().value();
            }

            responsePayload = safeJson(result);
            return result;

        } catch (Throwable throwable) {
            status = resolveStatusFromException(throwable);
            errorMessage = throwable.getMessage();
            throw throwable;

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

            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .action(resolveAction(logAction, httpMethod, joinPoint))
                    .entityName(resolveEntityName(logAction, joinPoint))
                    .entityId(resolveEntityId(request))
                    .httpMethod(httpMethod)
                    .endpoint(endpoint)
                    .requestPayload(requestPayload)
                    .responsePayload(responsePayload)
                    .executionTimeMs(executionTime)
                    .status(status)
                    .errorMessage(errorMessage)
                    .build();

            auditLogRepository.save(auditLog);
            if (request != null) {
                request.setAttribute(AUDIT_LOGGED_ATTRIBUTE, Boolean.TRUE);
            }

        } catch (Exception e) {
            log.error("Loi khi luu AuditLog (khong lam sap he thong chinh): {}", e.getMessage());
        }
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        return null;
    }

    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private HttpServletResponse getHttpServletResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getResponse() : null;
    }

    private LogAction resolveLogAction(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return AnnotatedElementUtils.findMergedAnnotation(method, LogAction.class);
    }

    private AuditLog.AuditAction resolveAction(LogAction logAction, String httpMethod, ProceedingJoinPoint joinPoint) {
        if (logAction != null && logAction.action() != null && !logAction.action().isBlank()) {
            AuditLog.AuditAction mappedAction = mapActionName(logAction.action(), httpMethod, joinPoint);
            if (mappedAction != null) {
                return mappedAction;
            }
        }

        return switch (httpMethod.toUpperCase(Locale.ROOT)) {
            case "GET", "HEAD" -> AuditLog.AuditAction.READ;
            case "DELETE" -> AuditLog.AuditAction.DELETE;
            case "PUT", "PATCH" -> AuditLog.AuditAction.UPDATE;
            case "POST" -> inferPostAction(joinPoint);
            default -> AuditLog.AuditAction.UPDATE;
        };
    }

    private AuditLog.AuditAction inferPostAction(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName().toLowerCase(Locale.ROOT);
        if (methodName.contains("login")) {
            return AuditLog.AuditAction.LOGIN;
        }
        if (methodName.contains("logout")) {
            return AuditLog.AuditAction.LOGOUT;
        }
        if (methodName.contains("register") || methodName.contains("signup")) {
            return AuditLog.AuditAction.SIGNUP;
        }
        if (methodName.contains("changepassword")) {
            return AuditLog.AuditAction.CHANGE_PASSWORD;
        }
        return AuditLog.AuditAction.CREATE;
    }

    private AuditLog.AuditAction mapActionName(String action, String httpMethod, ProceedingJoinPoint joinPoint) {
        String normalized = action.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "READ", "READ_ALL", "GET_PROFILE" -> AuditLog.AuditAction.READ;
            case "CREATE" -> AuditLog.AuditAction.CREATE;
            case "UPDATE", "REFRESH_TOKEN", "VERIFY_EMAIL", "RESEND_OTP", "FORGOT_PASSWORD",
                    "VERIFY_RESET_OTP", "RESET_PASSWORD", "SEND_OTP_EMAIL" -> AuditLog.AuditAction.UPDATE;
            case "DELETE" -> AuditLog.AuditAction.DELETE;
            case "LOGIN", "LOGIN_SUCCESS" -> AuditLog.AuditAction.LOGIN;
            case "LOGIN_FAILED" -> AuditLog.AuditAction.LOGIN_FAILED;
            case "LOGOUT", "LOGOUT_ALL" -> AuditLog.AuditAction.LOGOUT;
            case "REGISTER", "SIGNUP" -> AuditLog.AuditAction.SIGNUP;
            case "CHANGE_PASSWORD" -> AuditLog.AuditAction.CHANGE_PASSWORD;
            case "EXPORT" -> AuditLog.AuditAction.EXPORT;
            case "IMPORT" -> AuditLog.AuditAction.IMPORT;
            default -> switch (httpMethod.toUpperCase(Locale.ROOT)) {
                case "GET", "HEAD" -> AuditLog.AuditAction.READ;
                case "DELETE" -> AuditLog.AuditAction.DELETE;
                case "PUT", "PATCH" -> AuditLog.AuditAction.UPDATE;
                case "POST" -> inferPostAction(joinPoint);
                default -> null;
            };
        };
    }

    private String resolveEntityName(LogAction logAction, ProceedingJoinPoint joinPoint) {
        if (logAction != null && logAction.entityName() != null && !logAction.entityName().isBlank()) {
            return logAction.entityName().trim().toUpperCase(Locale.ROOT);
        }

        String controllerName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        if (controllerName.endsWith("Controller")) {
            controllerName = controllerName.substring(0, controllerName.length() - "Controller".length());
        }
        return CAMEL_CASE_BOUNDARY.matcher(controllerName)
                .replaceAll("_$1")
                .toUpperCase(Locale.ROOT);
    }

    @SuppressWarnings("unchecked")
    private String resolveEntityId(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        Object uriVariables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (!(uriVariables instanceof Map<?, ?> variables)) {
            return null;
        }

        Object prioritizedId = variables.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .filter(entry -> entry.getKey().toString().toLowerCase(Locale.ROOT).contains("id"))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);

        return prioritizedId != null ? prioritizedId.toString() : null;
    }

    private Integer resolveStatusFromException(Throwable throwable) {
        if (throwable instanceof BusinessException businessException) {
            return businessException.getErrorCode().getStatus();
        }
        return 500;
    }

    private String serializeToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return "Cannot serialize to JSON";
        }
    }

    private String maskSensitiveData(String payload) {
        if (payload == null) {
            return null;
        }

        return payload
                .replaceAll("(?i)\"password\"\\s*:\\s*\".*?\"", "\"password\":\"***\"")
                .replaceAll("(?i)\"token\"\\s*:\\s*\".*?\"", "\"token\":\"***\"")
                .replaceAll("(?i)\"accessToken\"\\s*:\\s*\".*?\"", "\"accessToken\":\"***\"");
    }

    private String safeJson(Object data) {
        String json = serializeToJson(data);
        json = maskSensitiveData(json);

        if (json != null && json.length() > 50000) {
            json = json.substring(0, 49997) + "...";
        }
        return json;
    }
}
