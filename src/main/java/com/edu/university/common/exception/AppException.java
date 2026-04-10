package com.edu.university.common.exception;

import lombok.Getter;

/**
 * Standardized application exception.
 * Replaces BusinessException for ControllerIT compatibility.
 */
@Getter
public class AppException extends BusinessException {

    public AppException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AppException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
