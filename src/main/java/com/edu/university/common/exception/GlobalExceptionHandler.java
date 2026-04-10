package com.edu.university.common.exception;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.auth.aspect.AuditLogAspect;
import com.edu.university.modules.auth.entity.AuditLog;
import com.edu.university.modules.auth.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Autowired(required = false)
    private AuditLogService auditLogService;

    // ===== UTIL =====
    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }

    private UUID getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl userDetails) {
                return userDetails.getId();
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }

    private void logAudit(AuditLog.AuditAction action, String entity, String entityId, Integer status, 
                          HttpServletRequest request, String message) {
        if (auditLogService != null && request.getAttribute(AuditLogAspect.AUDIT_LOGGED_ATTRIBUTE) == null) {
            auditLogService.log(
                    action,
                    entity,
                    entityId,
                    status,
                    request.getRequestURI(),
                    request.getMethod(),
                    getCurrentUserId(),
                    message,
                    request
            );
        }
    }

    // ===== 1. APP EXCEPTION (Standardized) =====
    @ExceptionHandler(AppException.class)
    public ResponseEntity<BaseResponse<Void>> handleAppException(
            AppException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        ErrorCode errorCode = ex.getErrorCode();
        String message = ex.getMessage();

        log.warn("[TraceID: {}] App Exception [{}]: {}", traceId,
                errorCode.getInternalCode(), message);

        logAudit(AuditLog.AuditAction.UPDATE, "SYSTEM", null, errorCode.getStatus(), request, message);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(BaseResponse.error(
                        errorCode.getStatus(),
                        errorCode.getInternalCode(),
                        message,
                        request.getRequestURI(),
                        traceId
                ));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Void>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        return handleAppException(new AppException(ex.getErrorCode(), ex.getMessage()), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(
                        400,
                        "VALIDATION_ERROR",
                        "Dữ liệu không hợp lệ",
                        errors,
                        request.getRequestURI(),
                        traceId
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Void>> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.warn("[TraceID: {}] Access Denied: {}", traceId, ex.getMessage());

        logAudit(AuditLog.AuditAction.UPDATE, "SECURITY", null, ErrorCode.FORBIDDEN.getStatus(), request, ex.getMessage());

        return ResponseEntity
                .status(ErrorCode.FORBIDDEN.getHttpStatus())
                .body(BaseResponse.error(
                        ErrorCode.FORBIDDEN.getStatus(),
                        ErrorCode.FORBIDDEN.getInternalCode(),
                        ErrorCode.FORBIDDEN.getMessage(),
                        request.getRequestURI(),
                        traceId
                ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<Void>> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[TraceID: {}] Runtime Exception: {}", traceId, ex.getMessage(), ex);

        logAudit(AuditLog.AuditAction.UPDATE, "SYSTEM", null, ErrorCode.INTERNAL_SERVER_ERROR.getStatus(), request, ex.getMessage());

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(BaseResponse.error(
                        ErrorCode.INTERNAL_SERVER_ERROR.getStatus(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getInternalCode(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        traceId
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[TraceID: {}] Internal Server Error: {}", traceId, ex.getMessage(), ex);

        logAudit(AuditLog.AuditAction.UPDATE, "SYSTEM", null, ErrorCode.INTERNAL_SERVER_ERROR.getStatus(), request, ex.getMessage());

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(BaseResponse.error(
                        ErrorCode.INTERNAL_SERVER_ERROR.getStatus(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getInternalCode(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                        request.getRequestURI(),
                        traceId
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        });

        log.warn("[TraceID: {}] Constraint Violation Error: {}", traceId, errors);

        logAudit(AuditLog.AuditAction.UPDATE, "USER", null, ErrorCode.INVALID_INPUT.getStatus(), request, errors.toString());

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT.getHttpStatus())
                .body(BaseResponse.error(
                        ErrorCode.INVALID_INPUT.getStatus(),
                        ErrorCode.INVALID_INPUT.getInternalCode(),
                        ErrorCode.INVALID_INPUT.getMessage(),
                        errors,
                        request.getRequestURI(),
                        traceId
                ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse<Void>> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.warn("[TraceID: {}] Bad Credentials: {}", traceId, ex.getMessage());

        logAudit(AuditLog.AuditAction.LOGIN_FAILED, "SECURITY", null, 400, request, ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(
                        400,
                        "AUTH_003",
                        "Thông tin đăng nhập không chính xác",
                        request.getRequestURI(),
                        traceId
                ));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleUserNotFound(
            UsernameNotFoundException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.warn("[TraceID: {}] User Not Found: {}", traceId, ex.getMessage());

        logAudit(AuditLog.AuditAction.LOGIN_FAILED, "SECURITY", null, 404, request, ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.error(
                        404,
                        "AUTH_002",
                        "Không tìm thấy người dùng",
                        request.getRequestURI(),
                        traceId
                ));
    }
}
