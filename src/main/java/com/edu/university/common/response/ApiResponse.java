package com.edu.university.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        int code,
        String errorCode,
        String message,
        T data,
        T details,
        String path,
        String traceId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp
) {
    // 1. HTTP 200 OK (ThÃ nh cÃ´ng chung)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, null, "ThÃ nh cÃ´ng", data, null, null, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, null, message, data, null, null, null, LocalDateTime.now());
    }

    // 2. HTTP 201 CREATED (Táº¡o má»›i thÃ nh cÃ´ng)
    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(201, null, message, data, null, null, null, LocalDateTime.now());
    }

    // 3. Lá»—i chung (KhÃ´ng chi tiáº¿t)
    public static <T> ApiResponse<T> error(int code, String errorCode, String message, String path, String traceId) {
        return new ApiResponse<>(code, errorCode, message, null, null, path, traceId, LocalDateTime.now());
    }

    // 4. Lá»—i Validation (CÃ³ chi tiáº¿t)
    public static <T> ApiResponse<T> error(int code, String errorCode, String message, T details, String path, String traceId) {
        return new ApiResponse<>(code, errorCode, message, null, details, path, traceId, LocalDateTime.now());
    }
}
