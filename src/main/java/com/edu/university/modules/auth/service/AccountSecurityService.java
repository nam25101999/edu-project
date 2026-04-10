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
    private final com.edu.university.common.service.RedisService redisService;
    private final PasswordEncoder encoder; // Dùng để hash OTP
    private final HttpServletRequest httpRequest;
    private final SecureRandom secureRandom = new SecureRandom();

    // =========================
    // 1. FORGOT & RESET PASSWORD
    // =========================

    @LogAction(action = "FORGOT_PASSWORD", entityName = "USER")
    @Transactional
    public void generateAndSendPasswordOtp(ForgotPasswordRequest request) {
        String email = request.email();
        String ip = getClientIp();

        // Layer 1: Anti-Spam (Redis Rate Limit)
        redisService.validateOtpRequest(email, ip);

        try {
            Users user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            if (!user.isActive()) {
                // Keep moving to fake success to avoid enumeration
                log.warn("Blocked OTP for inactive user email: {}", email);
                return;
            }

            sendOtpToUser(user, OtpToken.OtpType.RESET_PASSWORD);
        } catch (BusinessException e) {
            // Anti-enumeration: Mock success even if user not found
            log.info("Anti-enumeration mock success for email: {}", email);
        }
    }

    @LogAction(action = "RESET_PASSWORD", entityName = "USER")
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        Users user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản!"));

        validateAndConsumeOtp(user, request.otp(), OtpToken.OtpType.RESET_PASSWORD);

        // Đổi pass, tăng Token Version để kick toàn bộ session cũ
        user.setPassword(encoder.encode(request.newPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setLockUntil(null);
        user.setFailedLoginAttempts(0);
        user.incrementTokenVersion();

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public void verifyResetOtp(String email, String otp) {
        log.info("[DEBUG] Received OTP verification request for email: {}", email);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[DEBUG] User not found for email: {}", email);
                    return new BusinessException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản!");
                });

        // Lấy ra OTP mới nhất, chưa dùng, chưa bị thu hồi
        OtpToken otpToken = otpTokenRepo
                .findTopByUserAndOtpTypeAndIsUsedFalseAndIsRevokedFalseAndDeletedAtIsNullOrderByCreatedAtDesc(user,
                        OtpToken.OtpType.RESET_PASSWORD)
                .orElseThrow(() -> {
                    log.warn("[DEBUG] No valid RESET_PASSWORD OTP found for user: {}", email);
                    return new BusinessException(ErrorCode.OTP_INVALID,
                            "Bạn chưa yêu cầu mã OTP hoặc mã đã bị hủy!");
                });

        log.info("[DEBUG] Found OTP token created at: {}, expires at: {}", otpToken.getCreatedAt(),
                otpToken.getExpiresAt());

        if (otpToken.isExpired()) {
            log.warn("[DEBUG] OTP expired for user: {}", email);
            throw new BusinessException(ErrorCode.OTP_EXPIRED, "Mã OTP đã hết hạn!");
        }

        if (otpToken.isLockedOut()) {
            log.warn("[DEBUG] OTP locked out for user: {}", email);
            throw new BusinessException(ErrorCode.FORBIDDEN,
                    "OTP đã bị khóa do nhập sai quá nhiều lần. Vui lòng yêu cầu mã mới!");
        }

        // So khớp Hash
        boolean matches = encoder.matches(otp, otpToken.getOtpHash());
        log.info("[DEBUG] OTP match result: {}", matches);

        if (!matches) {
            throw new BusinessException(ErrorCode.OTP_INVALID, "Mã OTP không chính xác!");
        }

        log.info("[DEBUG] OTP verification successful for user: {}", email);
    }

    // =========================
    // 2. VERIFY EMAIL (ĐĂNG KÝ)
    // =========================

    @LogAction(action = "VERIFY_EMAIL", entityName = "USER")
    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        Users user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản"));

        if (user.isEmailVerified()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Tài khoản này đã được xác thực trước đó!");
        }

        validateAndConsumeOtp(user, request.otp(), OtpToken.OtpType.REGISTER);

        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setIsActive(true);
        userRepository.save(user);
    }

    // =========================
    // 3. RESEND OTP (DÙNG CHUNG)
    // =========================

    @LogAction(action = "RESEND_OTP", entityName = "USER")
    @Transactional
    public void resendOtp(ResendOtpRequest request) {
        String email = request.email();
        String ip = getClientIp();

        // Layer 1: Anti-Spam (Redis Rate Limit)
        redisService.validateOtpRequest(email, ip);

        try {
            Users user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            if (user.isEmailVerified()) {
                throw new BusinessException(ErrorCode.BAD_REQUEST,
                        "Tài khoản này đã được xác thực, không cần nhận thêm mã OTP nữa!");
            }

            // Mặc định resend cho REGISTER nếu không truyền type
            sendOtpToUser(user, OtpToken.OtpType.REGISTER);
        } catch (BusinessException e) {
            // Anti-enumeration for resend too
            if (e.getErrorCode() == ErrorCode.USER_NOT_FOUND) {
                log.info("Anti-enumeration mock success for resend to email: {}", email);
            } else {
                throw e;
            }
        }
    }

    // =========================
    // PRIVATE HELPER METHODS
    // =========================

    public void sendOtpToUser(Users user, OtpToken.OtpType type) {
        // 1. Thu hồi toàn bộ OTP cũ đang valid của user cho loại hình này
        // (Latest Only)
        otpTokenRepo.findByUserAndOtpTypeAndIsUsedFalseAndIsRevokedFalseAndDeletedAtIsNull(user, type)
                .forEach(otp -> {
                    otp.revoke();
                });

        // 2. Sinh OTP mới (plain text để gửi, hash để lưu)
        String plainOtp = String.format("%06d", secureRandom.nextInt(1000000));

        OtpToken otpToken = OtpToken.builder()
                .otpHash(encoder.encode(plainOtp)) // Lưu chuỗi Hash, tuyệt đối không lưu plain text
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
            log.error("Lỗi khi gửi OTP qua Email: ", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "Không thể kết nối dịch vụ Email. Vui lòng thử lại sau.");
        }
    }

    private void validateAndConsumeOtp(Users user, String incomingOtp, OtpToken.OtpType type) {
        // Lấy ra OTP mới nhất, chưa dùng, chưa bị thu hồi
        OtpToken otpToken = otpTokenRepo
                .findTopByUserAndOtpTypeAndIsUsedFalseAndIsRevokedFalseAndDeletedAtIsNullOrderByCreatedAtDesc(user,
                        type)
                .orElseThrow(() -> new BusinessException(ErrorCode.OTP_INVALID,
                        "Bạn chưa yêu cầu mã OTP hoặc mã đã bị hủy!"));

        if (otpToken.isExpired()) {
            throw new BusinessException(ErrorCode.OTP_EXPIRED, "Mã OTP đã hết hạn!");
        }

        if (otpToken.isLockedOut()) {
            throw new BusinessException(ErrorCode.FORBIDDEN,
                    "OTP đã bị khóa do nhập sai quá nhiều lần. Vui lòng yêu cầu mã mới!");
        }

        // So khớp Hash
        if (!encoder.matches(incomingOtp, otpToken.getOtpHash())) {
            otpToken.recordFailedAttempt();
            throw new BusinessException(ErrorCode.OTP_INVALID,
                    "Mã OTP không chính xác! Bạn còn " + (OtpToken.MAX_ATTEMPTS - otpToken.getAttemptCount())
                            + " lần thử.");
        }

        // Đánh dấu thành công
        otpToken.markAsUsed();
    }

    private String getClientIp() {
        if (httpRequest == null)
            return null;
        String ip = httpRequest.getHeader("X-Forwarded-For");
        return (ip == null || ip.isEmpty()) ? httpRequest.getRemoteAddr() : ip.split(",")[0];
    }

    private String getUserAgent() {
        return httpRequest != null ? httpRequest.getHeader("User-Agent") : null;
    }
}
