package com.edu.university.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * Gom nhÃ³m cÃ¡c Data Transfer Objects (DTO) liÃªn quan Ä‘áº¿n XÃ¡c thá»±c vÃ  Báº£o máº­t tÃ i khoáº£n.
 */
public class AuthDtos {

    // --- REQUESTS ---

    public record LoginRequest(
            @NotBlank(message = "Username khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng") String identifier,
            @NotBlank(message = "Password khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng") String password
    ) {
        // Tá»± Ä‘á»™ng trim khoáº£ng tráº¯ng thá»«a khi user copy/paste
        public LoginRequest {
            identifier = identifier != null ? identifier.trim() : null;
        }
    }

    public record SignupRequest(
            @NotBlank(message = "Username khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
            @Size(min = 3, max = 50, message = "Username pháº£i tá»« 3 Ä‘áº¿n 50 kÃ½ tá»±")
            String username,

            @NotBlank(message = "Password khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
            @Size(min = 8, message = "Password pháº£i cÃ³ Ã­t nháº¥t 8 kÃ½ tá»±")
            String password,

            @NotBlank(message = "Email khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
            @Email(message = "Email khÃ´ng Ä‘Ãºng Ä‘á»‹nh dáº¡ng")
            String email,

            String role
    ) {
        // Chuáº©n hÃ³a Username vÃ  Ä‘Æ°a Email vá» chá»¯ thÆ°á»ng
        public SignupRequest {
            username = username != null ? username.trim() : null;
            email = email != null ? email.trim().toLowerCase() : null;
        }
    }

    public record ChangePasswordRequest(
            @NotBlank(message = "Máº­t kháº©u cÅ© khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
            String oldPassword,

            @NotBlank(message = "Máº­t kháº©u má»›i khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
            @Size(min = 8, message = "Máº­t kháº©u má»›i pháº£i cÃ³ Ã­t nháº¥t 8 kÃ½ tá»±")
            String newPassword
    ) {}

    public record TokenRefreshRequest(
            @NotBlank(message = "Refresh Token khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
            String refreshToken
    ) {
        public TokenRefreshRequest {
            refreshToken = refreshToken != null ? refreshToken.trim() : null;
        }
    }

    // Bá»• sung LogoutRequest Ä‘á»ƒ map JSON body tá»« client khi gá»i API Ä‘Äƒng xuáº¥t
    public record LogoutRequest(
            @NotBlank(message = "Refresh Token khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
            String refreshToken
    ) {
        public LogoutRequest {
            refreshToken = refreshToken != null ? refreshToken.trim() : null;
        }
    }

    public record VerifyEmailRequest(
            @NotBlank(message = "Email khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng") @Email String email,
            @NotBlank(message = "MÃ£ OTP khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng") String otp
    ) {
        public VerifyEmailRequest {
            email = email != null ? email.trim().toLowerCase() : null;
            otp = otp != null ? otp.trim() : null;
        }
    }

    public record ResendOtpRequest(
            @NotBlank(message = "Email khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng") @Email String email
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

    // ================= DTOs DÃ€NH RIÃŠNG CHO CRUD USERS =================

    public record UserCreateRequest(
            @NotBlank(message = "TÃªn Ä‘Äƒng nháº­p khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng") String username,
            @NotBlank(message = "Email khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng") @Email(message = "Email khÃ´ng há»£p lá»‡") String email,
            @NotBlank(message = "Máº­t kháº©u khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng") String password,
            List<String> roles,
            boolean isActive
    ) {}

    public record UserUpdateRequest(
            @Email(message = "Email khÃ´ng há»£p lá»‡") String email,
            Boolean isActive,
            List<String> roles
    ) {}
}
