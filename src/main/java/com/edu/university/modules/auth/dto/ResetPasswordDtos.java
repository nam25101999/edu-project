package com.edu.university.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ResetPasswordDtos {

    public record ForgotPasswordRequest(
            @NotBlank(message = "Email không được để trống")
            @Email(message = "Email không hợp lệ")
            String email
    ) {
        // Canonical Constructor để tự động trim email khi nhận request
        public ForgotPasswordRequest {
            email = email != null ? email.trim().toLowerCase() : null;
        }
    }

    public record ResetPasswordRequest(
            @NotBlank(message = "Email không được để trống")
            @Email(message = "Email không hợp lệ")
            String email,

            @NotBlank(message = "Mã OTP không được để trống")
            @Size(min = 6, max = 6, message = "Mã OTP phải đúng 6 ký số")
            @Pattern(regexp = "^[0-9]*$", message = "Mã OTP chỉ được chứa chữ số")
            String otp,

            @NotBlank(message = "Mật khẩu mới không được để trống")
            @Size(min = 8, message = "Mật khẩu mới phải có ít nhất 8 ký tự")
            String newPassword
    ) {
        public ResetPasswordRequest {
            email = email != null ? email.trim().toLowerCase() : null;
        }
    }
}