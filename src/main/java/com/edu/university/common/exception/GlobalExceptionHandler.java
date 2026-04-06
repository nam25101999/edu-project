package com.edu.university.common.exception;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.auth.entity.AuditLog;
import com.edu.university.modules.auth.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final AuditLogService auditLogService;

    // ===== UTIL =====
    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }

    // Lấy UUID của User thay vì Username để phù hợp với chuẩn Log mới
    private UUID getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl userDetails) {
                return userDetails.getId();
            }
        } catch (Exception e) {
            // Ignore
        }
        return null; // Tráº£ vá» null náº¿u lÃ  khÃ¡ch (Guest) chÆ°a Ä‘Äƒng nháº­p
    }

    // ===== 1. BUSINESS EXCEPTION =====
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Void>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        ErrorCode errorCode = ex.getErrorCode();
        String message = ex.getCustomMessage() != null
                ? ex.getCustomMessage()
                : errorCode.getMessage();

        log.warn("[TraceID: {}] Business Exception [{}]: {}", traceId,
                errorCode.getInternalCode(), message);

        // ðŸ”¥ AUDIT LOG
        auditLogService.log(
                AuditLog.AuditAction.UPDATE, // DÃ¹ng UPDATE táº¡m (cÃ³ thá»ƒ thÃªm BUSINESS_ERROR vÃ o Enum)
                "SYSTEM",
                null,
                errorCode.getStatus(), // Status dáº¡ng Integer (400, 404,...)
                request.getRequestURI(),
                request.getMethod(),
                getCurrentUserId(),
                message,
                request
        );

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

    // ===== 2. VALIDATION ERROR =====
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

        log.warn("[TraceID: {}] Validation Error: {}", traceId, errors);

        // ðŸ”¥ AUDIT LOG
        auditLogService.log(
                AuditLog.AuditAction.UPDATE,
                "USER",
                null,
                ErrorCode.INVALID_INPUT.getStatus(),
                request.getRequestURI(),
                request.getMethod(),
                getCurrentUserId(),
                errors.toString(),
                request
        );

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

    // ===== 3. ACCESS DENIED =====
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Void>> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("[TraceID: {}] Access Denied: {}", traceId, ex.getMessage());

        // ðŸ”¥ AUDIT LOG
        auditLogService.log(
                AuditLog.AuditAction.UPDATE,
                "SECURITY",
                null,
                ErrorCode.FORBIDDEN.getStatus(),
                request.getRequestURI(),
                request.getMethod(),
                getCurrentUserId(),
                ex.getMessage(),
                request
        );

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

    // ===== 4. RUNTIME EXCEPTION =====
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<Void>> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();

        log.error("[TraceID: {}] Runtime Exception: {}", traceId, ex.getMessage(), ex);

        // ðŸ”¥ AUDIT LOG
        auditLogService.log(
                AuditLog.AuditAction.UPDATE,
                "SYSTEM",
                null,
                ErrorCode.INTERNAL_SERVER_ERROR.getStatus(),
                request.getRequestURI(),
                request.getMethod(),
                getCurrentUserId(),
                ex.getMessage(),
                request
        );

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

    // ===== 5. GLOBAL EXCEPTION =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();

        log.error("[TraceID: {}] Internal Server Error: {}", traceId, ex.getMessage(), ex);

        // ðŸ”¥ AUDIT LOG
        auditLogService.log(
                AuditLog.AuditAction.UPDATE,
                "SYSTEM",
                null,
                ErrorCode.INTERNAL_SERVER_ERROR.getStatus(),
                request.getRequestURI(),
                request.getMethod(),
                getCurrentUserId(),
                ex.getMessage(),
                request
        );

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

    // ===== 6. CONSTRAINT VIOLATION (Jakarta) =====
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

        // ðŸ”¥ AUDIT LOG
        auditLogService.log(
                AuditLog.AuditAction.UPDATE,
                "USER",
                null,
                ErrorCode.INVALID_INPUT.getStatus(),
                request.getRequestURI(),
                request.getMethod(),
                getCurrentUserId(),
                errors.toString(),
                request
        );

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

    // ===== 7. BAD CREDENTIALS =====
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse<Void>> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("[TraceID: {}] Bad Credentials: {}", traceId, ex.getMessage());

        // ðŸ”¥ Audit log
        auditLogService.log(
                AuditLog.AuditAction.LOGIN_FAILED, // Ãnh xáº¡ chuáº©n vá»›i Enum LOGIN_FAILED
                "SECURITY",
                null,
                400,
                request.getRequestURI(),
                request.getMethod(),
                getCurrentUserId(),
                ex.getMessage(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(
                        400,
                        "AUTH_003", // INVALID_CREDENTIALS
                        "ThÃ´ng tin Ä‘Äƒng nháº­p khÃ´ng chÃ­nh xÃ¡c",
                        request.getRequestURI(),
                        traceId
                ));
    }

    // ===== 7. USER NOT FOUND =====
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleUserNotFound(
            UsernameNotFoundException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("[TraceID: {}] User Not Found: {}", traceId, ex.getMessage());

        // ðŸ”¥ Audit log
        auditLogService.log(
                AuditLog.AuditAction.LOGIN_FAILED, // Ãnh xáº¡ chuáº©n vá»›i Enum LOGIN_FAILED
                "SECURITY",
                null,
                404,
                request.getRequestURI(),
                request.getMethod(),
                getCurrentUserId(),
                ex.getMessage(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.error(
                        404,
                        "AUTH_002", // USER_NOT_FOUND
                        "KhÃ´ng tÃ¬m tháº¥y ngÆ°á»i dÃ¹ng",
                        request.getRequestURI(),
                        traceId
                ));
    }
}
