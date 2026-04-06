package com.edu.university.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ResetPasswordDtos {

    public record ForgotPasswordRequest(
            @NotBlank(message = "Email khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
            @Email(message = "Email khÃ´ng há»£p lá»‡")
            String email
    ) {
        // Canonical Constructor Ä‘á»ƒ tá»± Ä‘á»™ng trim email khi nháº­n request
        public ForgotPasswordRequest {
            email = email != null ? email.trim().toLowerCase() : null;
        }
    }

    public record ResetPasswordRequest(
            @NotBlank(message = "Email khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
            @Email(message = "Email khÃ´ng há»£p lá»‡")
            String email,

            @NotBlank(message = "MÃ£ OTP khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
            @Size(min = 6, max = 6, message = "MÃ£ OTP pháº£i Ä‘Ãºng 6 kÃ½ sá»‘")
            @Pattern(regexp = "^[0-9]*$", message = "MÃ£ OTP chá»‰ Ä‘Æ°á»£c chá»©a chá»¯ sá»‘")
            String otp,

            @NotBlank(message = "Máº­t kháº©u má»›i khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
            @Size(min = 8, message = "Máº­t kháº©u má»›i pháº£i cÃ³ Ã­t nháº¥t 8 kÃ½ tá»±")
            String newPassword
    ) {
        public ResetPasswordRequest {
            email = email != null ? email.trim().toLowerCase() : null;
        }
    }
}
