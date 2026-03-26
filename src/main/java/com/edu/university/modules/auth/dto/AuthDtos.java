package com.edu.university.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Gom nhóm các Data Transfer Objects (DTO) liên quan đến Xác thực và Bảo mật tài khoản.
 */
public class AuthDtos {

    // --- REQUESTS ---

    public record LoginRequest(
            @NotBlank(message = "Username không được để trống") String identifier,
            @NotBlank(message = "Password không được để trống") String password
    ) {}

    public record SignupRequest(
            @NotBlank(message = "Username không được để trống")
            @Size(min = 3, max = 50, message = "Username phải từ 3 đến 50 ký tự")
            String username,

            @NotBlank(message = "Password không được để trống")
            @Size(min = 8, message = "Password phải có nhất 8 ký tự")
            String password,

            @NotBlank(message = "Email không được để trống")
            @Email(message = "Email không đúng định dạng")
            String email,

            String role
    ) {}

    // Thêm Request đổi mật khẩu
    public record ChangePasswordRequest(
            @NotBlank(message = "Mật khẩu cũ không được để trống")
            String oldPassword,

            @NotBlank(message = "Mật khẩu mới không được để trống")
            @Size(min = 8, message = "Mật khẩu mới phải có ít nhất 8 ký tự")
            String newPassword
    ) {}

    public record TokenRefreshRequest(
            @NotBlank(message = "Refresh Token không được để trống")
            String refreshToken
    ) {}

    public record VerifyEmailRequest(
            @NotBlank(message = "Email không được để trống") @Email String email,
            @NotBlank(message = "Mã OTP không được để trống") String otp
    ) {}

    public record ResendOtpRequest(
            @NotBlank(message = "Email không được để trống") @Email String email
    ) {}

    // --- RESPONSES ---

    public record JwtResponse(
            String accessToken,
            String refreshToken,
            UUID id,
            String username,
            String role,
            String tokenType
    ) {
        public JwtResponse(String accessToken, String refreshToken, UUID id, String username, String role) {
            this(accessToken, refreshToken, id, username, role, "Bearer");
        }
    }

    public record TokenRefreshResponse(
            String accessToken,
            String refreshToken,
            String tokenType
    ) {
        public TokenRefreshResponse(String accessToken, String refreshToken) {
            this(accessToken, refreshToken, "Bearer");
        }
    }
}