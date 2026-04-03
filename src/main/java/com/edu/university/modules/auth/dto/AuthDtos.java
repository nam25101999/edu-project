package com.edu.university.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * Gom nhóm các Data Transfer Objects (DTO) liên quan đến Xác thực và Bảo mật tài khoản.
 */
public class AuthDtos {

    // --- REQUESTS ---

    public record LoginRequest(
            @NotBlank(message = "Username không được để trống") String identifier,
            @NotBlank(message = "Password không được để trống") String password
    ) {
        // Tự động trim khoảng trắng thừa khi user copy/paste
        public LoginRequest {
            identifier = identifier != null ? identifier.trim() : null;
        }
    }

    public record SignupRequest(
            @NotBlank(message = "Username không được để trống")
            @Size(min = 3, max = 50, message = "Username phải từ 3 đến 50 ký tự")
            String username,

            @NotBlank(message = "Password không được để trống")
            @Size(min = 8, message = "Password phải có ít nhất 8 ký tự")
            String password,

            @NotBlank(message = "Email không được để trống")
            @Email(message = "Email không đúng định dạng")
            String email,

            String role
    ) {
        // Chuẩn hóa Username và đưa Email về chữ thường
        public SignupRequest {
            username = username != null ? username.trim() : null;
            email = email != null ? email.trim().toLowerCase() : null;
        }
    }

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
    ) {
        public TokenRefreshRequest {
            refreshToken = refreshToken != null ? refreshToken.trim() : null;
        }
    }

    // Bổ sung LogoutRequest để map JSON body từ client khi gọi API đăng xuất
    public record LogoutRequest(
            @NotBlank(message = "Refresh Token không được để trống")
            String refreshToken
    ) {
        public LogoutRequest {
            refreshToken = refreshToken != null ? refreshToken.trim() : null;
        }
    }

    public record VerifyEmailRequest(
            @NotBlank(message = "Email không được để trống") @Email String email,
            @NotBlank(message = "Mã OTP không được để trống") String otp
    ) {
        public VerifyEmailRequest {
            email = email != null ? email.trim().toLowerCase() : null;
            otp = otp != null ? otp.trim() : null;
        }
    }

    public record ResendOtpRequest(
            @NotBlank(message = "Email không được để trống") @Email String email
    ) {
        public ResendOtpRequest {
            email = email != null ? email.trim().toLowerCase() : null;
        }
    }

    // --- RESPONSES ---

    public record JwtResponse(
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresIn,
            UserInfo user
    ) {
        public record UserInfo(
                UUID id,
                String username,
                java.util.List<String> roles
        ) {}
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

    // ================= DTOs DÀNH RIÊNG CHO CRUD USERS =================

    public record UserCreateRequest(
            @NotBlank(message = "Tên đăng nhập không được để trống") String username,
            @NotBlank(message = "Email không được để trống") @Email(message = "Email không hợp lệ") String email,
            @NotBlank(message = "Mật khẩu không được để trống") String password,
            List<String> roles,
            boolean isActive
    ) {}

    public record UserUpdateRequest(
            @Email(message = "Email không hợp lệ") String email,
            Boolean isActive,
            List<String> roles
    ) {}
}