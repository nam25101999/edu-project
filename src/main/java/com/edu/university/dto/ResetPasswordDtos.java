package com.edu.university.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ResetPasswordDtos {

    public record ForgotPasswordRequest(
            @NotBlank(message = "Email không được để trống")
            @Email(message = "Email không hợp lệ")
            String email
    ) {}

    public record ResetPasswordRequest(
            @NotBlank(message = "Email không được để trống")
            @Email(message = "Email không hợp lệ")
            String email,

            @NotBlank(message = "Mã OTP không được để trống")
            String otp,

            @NotBlank(message = "Mật khẩu mới không được để trống")
            String newPassword
    ) {}
}