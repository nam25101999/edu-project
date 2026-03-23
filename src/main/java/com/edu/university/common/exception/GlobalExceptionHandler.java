package com.edu.university.common.exception;

import com.edu.university.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Hàm tự động sinh ra một mã duy nhất cho mỗi Request bị lỗi
    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }

    // 1. Lỗi Nghiệp Vụ (Business Exception) - Trả đúng HTTP Status đã config trong Enum (404, 409, 400...)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        ErrorCode errorCode = ex.getErrorCode();
        String message = ex.getCustomMessage() != null ? ex.getCustomMessage() : errorCode.getMessage();

        log.warn("[TraceID: {}] Business Exception [{}]: {}", traceId, errorCode.getInternalCode(), message);

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

    // 2. Lỗi Runtime (Lỗi do code Java thông thường văng ra) - 500 INTERNAL SERVER ERROR
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("[TraceID: {}] Runtime Exception: {}", traceId, ex.getMessage(), ex);

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

    // 3. LỖI VALIDATION (Dữ liệu gửi lên không đúng format @Valid) - 400 BAD REQUEST
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String traceId = generateTraceId();
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("[TraceID: {}] Validation Error: {}", traceId, errors);
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

    // 4. Lỗi Phân quyền Spring Security - 403 FORBIDDEN
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.warn("[TraceID: {}] Access Denied: {}", traceId, ex.getMessage());

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

    // 5. Lỗi Hệ thống nội bộ (Bắt mọi lỗi crash hệ thống còn lại) - 500 INTERNAL SERVER ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("[TraceID: {}] Internal Server Error: {}", traceId, ex.getMessage(), ex);

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
}