package com.edu.university.common.exception;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.report.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final AuditLogService auditLogService;

    // ===== UTIL =====
    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }

    private String getUsername() {
        try {
            return SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getName();
        } catch (Exception e) {
            return "anonymous";
        }
    }

    // ===== 1. BUSINESS EXCEPTION =====
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        ErrorCode errorCode = ex.getErrorCode();
        String message = ex.getCustomMessage() != null
                ? ex.getCustomMessage()
                : errorCode.getMessage();

        log.warn("[TraceID: {}] Business Exception [{}]: {}", traceId,
                errorCode.getInternalCode(), message);

        // 🔥 AUDIT LOG
        auditLogService.log(
                "BUSINESS_ERROR",
                "SYSTEM",
                "FAILED",
                request.getRequestURI(),
                request.getMethod(),
                getUsername(),
                message,
                request
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(
                        errorCode.getStatus(),
                        errorCode.getInternalCode(),
                        message,
                        request.getRequestURI(),
                        traceId
                ));
    }

    // ===== 2. VALIDATION ERROR =====
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("[TraceID: {}] Validation Error: {}", traceId, errors);

        // 🔥 AUDIT LOG (QUAN TRỌNG NHẤT)
        auditLogService.log(
                "VALIDATION_ERROR",
                "User",
                "FAILED",
                request.getRequestURI(),
                request.getMethod(),
                getUsername(),
                errors.toString(),
                request
        );

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT.getHttpStatus())
                .body(ApiResponse.error(
                        ErrorCode.INVALID_INPUT.getStatus(),
                        ErrorCode.INVALID_INPUT.getInternalCode(),
                        ErrorCode.INVALID_INPUT.getMessage(),
                        errors,
                        request.getRequestURI(),
                        traceId
                ));
    }

    // ===== 3. ACCESS DENIED =====
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("[TraceID: {}] Access Denied: {}", traceId, ex.getMessage());

        // 🔥 AUDIT LOG
        auditLogService.log(
                "ACCESS_DENIED",
                "SECURITY",
                "FAILED",
                request.getRequestURI(),
                request.getMethod(),
                getUsername(),
                ex.getMessage(),
                request
        );

        return ResponseEntity
                .status(ErrorCode.FORBIDDEN.getHttpStatus())
                .body(ApiResponse.error(
                        ErrorCode.FORBIDDEN.getStatus(),
                        ErrorCode.FORBIDDEN.getInternalCode(),
                        ErrorCode.FORBIDDEN.getMessage(),
                        request.getRequestURI(),
                        traceId
                ));
    }

    // ===== 4. RUNTIME EXCEPTION =====
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();

        log.error("[TraceID: {}] Runtime Exception: {}", traceId, ex.getMessage(), ex);

        // 🔥 AUDIT LOG
        auditLogService.log(
                "RUNTIME_ERROR",
                "SYSTEM",
                "FAILED",
                request.getRequestURI(),
                request.getMethod(),
                getUsername(),
                ex.getMessage(),
                request
        );

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ApiResponse.error(
                        ErrorCode.INTERNAL_SERVER_ERROR.getStatus(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getInternalCode(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        traceId
                ));
    }

    // ===== 5. GLOBAL EXCEPTION =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();

        log.error("[TraceID: {}] Internal Server Error: {}", traceId, ex.getMessage(), ex);

        // 🔥 AUDIT LOG
        auditLogService.log(
                "SYSTEM_ERROR",
                "SYSTEM",
                "FAILED",
                request.getRequestURI(),
                request.getMethod(),
                getUsername(),
                ex.getMessage(),
                request
        );

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ApiResponse.error(
                        ErrorCode.INTERNAL_SERVER_ERROR.getStatus(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getInternalCode(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                        request.getRequestURI(),
                        traceId
                ));
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("[TraceID: {}] Bad Credentials: {}", traceId, ex.getMessage());

        // 🔥 Audit log
        auditLogService.log(
                "AUTH_ERROR",
                "SECURITY",
                "FAILED",
                request.getRequestURI(),
                request.getMethod(),
                getUsername(),
                ex.getMessage(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        400,
                        "AUTH_003", // INVALID_CREDENTIALS
                        "Thông tin đăng nhập không chính xác",
                        request.getRequestURI(),
                        traceId
                ));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(
            UsernameNotFoundException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("[TraceID: {}] User Not Found: {}", traceId, ex.getMessage());

        // 🔥 Audit log
        auditLogService.log(
                "AUTH_ERROR",
                "SECURITY",
                "FAILED",
                request.getRequestURI(),
                request.getMethod(),
                getUsername(),
                ex.getMessage(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(
                        404,
                        "AUTH_002", // USER_NOT_FOUND
                        "Không tìm thấy người dùng",
                        request.getRequestURI(),
                        traceId
                ));
    }
}