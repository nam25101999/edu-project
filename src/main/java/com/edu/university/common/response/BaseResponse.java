package com.edu.university.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standardized API response structure for production-ready applications.
 *
 * @param <T> Type of the data being returned.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

    private boolean success;
    private int code;
    private String errorCode;
    private String message;
    private T data;
    private T details;
    private String path;
    private String traceId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // --- Static helper methods for common responses ---

    public static <T> BaseResponse<T> ok(T data) {
        return BaseResponse.<T>builder()
                .success(true)
                .message("Operation successful")
                .data(data)
                .code(200)
                .build();
    }

    public static <T> BaseResponse<T> ok(String message, T data) {
        return BaseResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .code(200)
                .build();
    }

    public static <T> BaseResponse<T> created(String message, T data) {
        return BaseResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .code(201)
                .build();
    }

    public static <T> BaseResponse<T> error(int code, String message) {
        return BaseResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .build();
    }

    public static <T> BaseResponse<T> error(int code, String errorCode, String message, String path, String traceId) {
        return BaseResponse.<T>builder()
                .success(false)
                .code(code)
                .errorCode(errorCode)
                .message(message)
                .path(path)
                .traceId(traceId)
                .build();
    }

    public static <T> BaseResponse<T> error(int code, String errorCode, String message, T details, String path, String traceId) {
        return BaseResponse.<T>builder()
                .success(false)
                .code(code)
                .errorCode(errorCode)
                .message(message)
                .details(details)
                .path(path)
                .traceId(traceId)
                .build();
    }
}
