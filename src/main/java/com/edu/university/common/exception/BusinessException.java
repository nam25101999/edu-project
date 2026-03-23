package com.edu.university.common.exception;

import lombok.Getter;

/**
 * Exception tùy chỉnh dành riêng cho các lỗi nghiệp vụ logic.
 * Giờ đây sẽ gắn liền với ErrorCode để chuẩn hóa đầu ra.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String customMessage;

    // Khởi tạo chỉ với ErrorCode có sẵn
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    // Khởi tạo với ErrorCode nhưng muốn ghi đè Message linh hoạt hơn
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }
}