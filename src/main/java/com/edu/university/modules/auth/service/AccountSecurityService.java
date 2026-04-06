package com.edu.university.modules.auth.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.dto.AuthDtos.ResendOtpRequest;
import com.edu.university.modules.auth.dto.AuthDtos.VerifyEmailRequest;
import com.edu.university.modules.auth.dto.ResetPasswordDtos.ForgotPasswordRequest;
import com.edu.university.modules.auth.dto.ResetPasswordDtos.ResetPasswordRequest;
import com.edu.university.modules.auth.entity.OtpToken;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.OtpTokenRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.auth.annotation.LogAction;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSecurityService {

    private final UserRepository userRepository;
    private final OtpTokenRepository otpTokenRepo;
    private final EmailService emailService;
    private final PasswordEncoder encoder; // DÃ¹ng Ä‘á»ƒ hash OTP
    private final HttpServletRequest httpRequest;
    private final SecureRandom secureRandom = new SecureRandom();

    // =========================
    // 1. FORGOT & RESET PASSWORD
    // =========================

    @LogAction(action = "FORGOT_PASSWORD", entityName = "USER")
    @Transactional
    public void generateAndSendPasswordOtp(ForgotPasswordRequest request) {
        Users user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "KhÃ´ng tÃ¬m tháº¥y tÃ i khoáº£n vá»›i email nÃ y!"));

        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "TÃ i khoáº£n cá»§a báº¡n chÆ°a Ä‘Æ°á»£c kÃ­ch hoáº¡t hoáº·c Ä‘Ã£ bá»‹ khÃ³a!");
        }

        sendOtpToUser(user, OtpToken.OtpType.RESET_PASSWORD);
    }

    @LogAction(action = "RESET_PASSWORD", entityName = "USER")
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        Users user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "KhÃ´ng tÃ¬m tháº¥y tÃ i khoáº£n!"));

        validateAndConsumeOtp(user, request.otp(), OtpToken.OtpType.RESET_PASSWORD);

        // Äá»•i pass, tÄƒng Token Version Ä‘á»ƒ kick toÃ n bá»™ session cÅ©
        user.setPassword(encoder.encode(request.newPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setLockUntil(null);
        user.setFailedLoginAttempts(0);
        user.incrementTokenVersion();

        userRepository.save(user);
    }

    // =========================
    // 2. VERIFY EMAIL (ÄÄ‚NG KÃ)
    // =========================

    @LogAction(action = "VERIFY_EMAIL", entityName = "USER")
    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        Users user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "KhÃ´ng tÃ¬m tháº¥y tÃ i khoáº£n!"));

        if (user.isEmailVerified()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "TÃ i khoáº£n nÃ y Ä‘Ã£ Ä‘Æ°á»£c xÃ¡c thá»±c trÆ°á»›c Ä‘Ã³!");
        }

        validateAndConsumeOtp(user, request.otp(), OtpToken.OtpType.REGISTER);

        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setActive(true);
        userRepository.save(user);
    }

    // =========================
    // 3. RESEND OTP (DÃ™NG CHUNG)
    // =========================

    @LogAction(action = "RESEND_OTP", entityName = "USER")
    @Transactional
    public void resendOtp(ResendOtpRequest request) {
        Users user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "KhÃ´ng tÃ¬m tháº¥y tÃ i khoáº£n!"));

        // Bá»” SUNG CHECK: KhÃ´ng gá»­i láº¡i OTP Ä‘Äƒng kÃ½ náº¿u tÃ i khoáº£n Ä‘Ã£ xÃ¡c thá»±c
        if (user.isEmailVerified()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "TÃ i khoáº£n nÃ y Ä‘Ã£ Ä‘Æ°á»£c xÃ¡c thá»±c, khÃ´ng cáº§n nháº­n thÃªm mÃ£ OTP ná»¯a!");
        }

        // Giáº£ sá»­ gá»i resend cho Register máº·c Ä‘á»‹nh (CÃ³ thá»ƒ má»Ÿ rá»™ng DTO Ä‘á»ƒ truyá»n type)
        sendOtpToUser(user, OtpToken.OtpType.REGISTER);
    }

    // =========================
    // PRIVATE HELPER METHODS
    // =========================

    public void sendOtpToUser(Users user, OtpToken.OtpType type) {
        // 1. Thu há»“i toÃ n bá»™ OTP cÅ© Ä‘ang valid cá»§a user cho loáº¡i hÃ¬nh nÃ y (Latest Only)
        // YÃªu cáº§u repo cÃ³ hÃ m: findValidOtpsByUserAndType(user, type)
        otpTokenRepo.findByUserAndOtpTypeAndIsUsedFalseAndIsRevokedFalseAndDeletedAtIsNull(user, type)
                .forEach(otp -> {
                    // Cáº§n Ä‘áº£m báº£o entity OtpToken cÃ³ hÃ m revoke() Ä‘á»ƒ set isRevoked = true
                    otp.revoke();
                });

        // KhÃ´ng cáº§n gá»i otpTokenRepo.save(otp) trong vÃ²ng láº·p vÃ¬ cÃ¡c entity OTP nÃ y Ä‘Ã£ Ä‘Æ°á»£c
        // Hibernate quáº£n lÃ½ (Managed Entities) nhá» @Transactional. NÃ³ sáº½ tá»± update xuá»‘ng DB.

        // 2. Sinh OTP má»›i (plain text Ä‘á»ƒ gá»­i, hash Ä‘á»ƒ lÆ°u)
        String plainOtp = String.format("%06d", secureRandom.nextInt(1000000));

        OtpToken otpToken = OtpToken.builder()
                .otpHash(encoder.encode(plainOtp)) // LÆ°u chuá»—i Hash, tuyá»‡t Ä‘á»‘i khÃ´ng lÆ°u plain text
                .otpType(type)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .ipAddress(getClientIp())
                .userAgent(getUserAgent())
                .build();

        otpTokenRepo.save(otpToken);

        try {
            emailService.sendOtpEmail(user.getEmail(), plainOtp);
        } catch (Exception e) {
            log.error("Lá»—i khi gá»­i OTP qua Email: ", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "KhÃ´ng thá»ƒ káº¿t ná»‘i dá»‹ch vá»¥ Email. Vui lÃ²ng thá»­ láº¡i sau.");
        }
    }

    private void validateAndConsumeOtp(Users user, String incomingOtp, OtpToken.OtpType type) {
        // Láº¥y ra OTP má»›i nháº¥t, chÆ°a dÃ¹ng, chÆ°a bá»‹ thu há»“i
        // YÃªu cáº§u repo cÃ³ hÃ m: findLatestValidOtp(user, type)
        OtpToken otpToken = otpTokenRepo.findTopByUserAndOtpTypeAndIsUsedFalseAndIsRevokedFalseAndDeletedAtIsNullOrderByCreatedAtDesc(user, type)
                .orElseThrow(() -> new BusinessException(ErrorCode.OTP_INVALID, "Báº¡n chÆ°a yÃªu cáº§u mÃ£ OTP hoáº·c mÃ£ Ä‘Ã£ bá»‹ há»§y!"));

        if (otpToken.isExpired()) {
            throw new BusinessException(ErrorCode.OTP_EXPIRED, "MÃ£ OTP Ä‘Ã£ háº¿t háº¡n!");
        }

        if (otpToken.isLockedOut()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "OTP Ä‘Ã£ bá»‹ khÃ³a do nháº­p sai quÃ¡ nhiá»u láº§n. Vui lÃ²ng yÃªu cáº§u mÃ£ má»›i!");
        }

        // So khá»›p Hash
        if (!encoder.matches(incomingOtp, otpToken.getOtpHash())) {
            otpToken.recordFailedAttempt();
            // otpTokenRepo.save(otpToken); // KhÃ´ng cáº§n thiáº¿t vÃ¬ cÃ³ @Transactional
            throw new BusinessException(ErrorCode.OTP_INVALID,
                    "MÃ£ OTP khÃ´ng chÃ­nh xÃ¡c! Báº¡n cÃ²n " + (OtpToken.MAX_ATTEMPTS - otpToken.getAttemptCount()) + " láº§n thá»­.");
        }

        // ÄÃ¡nh dáº¥u thÃ nh cÃ´ng
        otpToken.markAsUsed();
        // otpTokenRepo.save(otpToken); // KhÃ´ng cáº§n thiáº¿t vÃ¬ cÃ³ @Transactional
    }

    private String getClientIp() {
        if (httpRequest == null) return null;
        String ip = httpRequest.getHeader("X-Forwarded-For");
        return (ip == null || ip.isEmpty()) ? httpRequest.getRemoteAddr() : ip.split(",")[0];
    }

    private String getUserAgent() {
        return httpRequest != null ? httpRequest.getHeader("User-Agent") : null;
    }
}
