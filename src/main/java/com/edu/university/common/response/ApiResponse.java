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
    // 1. HTTP 200 OK (Thành công chung)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, null, "Thành công", data, null, null, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, null, message, data, null, null, null, LocalDateTime.now());
    }

    // 2. HTTP 201 CREATED (Tạo mới thành công)
    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(201, null, message, data, null, null, null, LocalDateTime.now());
    }

    // 3. Lỗi chung (Không chi tiết)
    public static <T> ApiResponse<T> error(int code, String errorCode, String message, String path, String traceId) {
        return new ApiResponse<>(code, errorCode, message, null, null, path, traceId, LocalDateTime.now());
    }

    // 4. Lỗi Validation (Có chi tiết)
    public static <T> ApiResponse<T> error(int code, String errorCode, String message, T details, String path, String traceId) {
        return new ApiResponse<>(code, errorCode, message, null, details, path, traceId, LocalDateTime.now());
    }
}