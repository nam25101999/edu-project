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
    private final PasswordEncoder encoder; // Dùng để hash OTP
    private final HttpServletRequest httpRequest;
    private final SecureRandom secureRandom = new SecureRandom();

    // =========================
    // 1. FORGOT & RESET PASSWORD
    // =========================

    @LogAction(action = "FORGOT_PASSWORD", entityName = "USER")
    @Transactional
    public void generateAndSendPasswordOtp(ForgotPasswordRequest request) {
        Users user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản với email này!"));

        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Tài khoản của bạn chưa được kích hoạt hoặc đã bị khóa!");
        }

        sendOtpToUser(user, OtpToken.OtpType.RESET_PASSWORD);
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

    // =========================
    // 2. VERIFY EMAIL (ĐĂNG KÝ)
    // =========================

    @LogAction(action = "VERIFY_EMAIL", entityName = "USER")
    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        Users user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản!"));

        if (user.isEmailVerified()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Tài khoản này đã được xác thực trước đó!");
        }

        validateAndConsumeOtp(user, request.otp(), OtpToken.OtpType.REGISTER);

        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setActive(true);
        userRepository.save(user);
    }

    // =========================
    // 3. RESEND OTP (DÙNG CHUNG)
    // =========================

    @LogAction(action = "RESEND_OTP", entityName = "USER")
    @Transactional
    public void resendOtp(ResendOtpRequest request) {
        Users user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản!"));

        // BỔ SUNG CHECK: Không gửi lại OTP đăng ký nếu tài khoản đã xác thực
        if (user.isEmailVerified()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Tài khoản này đã được xác thực, không cần nhận thêm mã OTP nữa!");
        }

        // Giả sử gọi resend cho Register mặc định (Có thể mở rộng DTO để truyền type)
        sendOtpToUser(user, OtpToken.OtpType.REGISTER);
    }

    // =========================
    // PRIVATE HELPER METHODS
    // =========================

    public void sendOtpToUser(Users user, OtpToken.OtpType type) {
        // 1. Thu hồi toàn bộ OTP cũ đang valid của user cho loại hình này (Latest Only)
        // Yêu cầu repo có hàm: findValidOtpsByUserAndType(user, type)
        otpTokenRepo.findByUserAndOtpTypeAndIsUsedFalseAndIsRevokedFalseAndDeletedAtIsNull(user, type)
                .forEach(otp -> {
                    // Cần đảm bảo entity OtpToken có hàm revoke() để set isRevoked = true
                    otp.revoke();
                });

        // Không cần gọi otpTokenRepo.save(otp) trong vòng lặp vì các entity OTP này đã được
        // Hibernate quản lý (Managed Entities) nhờ @Transactional. Nó sẽ tự update xuống DB.

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
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Không thể kết nối dịch vụ Email. Vui lòng thử lại sau.");
        }
    }

    private void validateAndConsumeOtp(Users user, String incomingOtp, OtpToken.OtpType type) {
        // Lấy ra OTP mới nhất, chưa dùng, chưa bị thu hồi
        // Yêu cầu repo có hàm: findLatestValidOtp(user, type)
        OtpToken otpToken = otpTokenRepo.findTopByUserAndOtpTypeAndIsUsedFalseAndIsRevokedFalseAndDeletedAtIsNullOrderByCreatedAtDesc(user, type)
                .orElseThrow(() -> new BusinessException(ErrorCode.OTP_INVALID, "Bạn chưa yêu cầu mã OTP hoặc mã đã bị hủy!"));

        if (otpToken.isExpired()) {
            throw new BusinessException(ErrorCode.OTP_EXPIRED, "Mã OTP đã hết hạn!");
        }

        if (otpToken.isLockedOut()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "OTP đã bị khóa do nhập sai quá nhiều lần. Vui lòng yêu cầu mã mới!");
        }

        // So khớp Hash
        if (!encoder.matches(incomingOtp, otpToken.getOtpHash())) {
            otpToken.recordFailedAttempt();
            // otpTokenRepo.save(otpToken); // Không cần thiết vì có @Transactional
            throw new BusinessException(ErrorCode.OTP_INVALID,
                    "Mã OTP không chính xác! Bạn còn " + (OtpToken.MAX_ATTEMPTS - otpToken.getAttemptCount()) + " lần thử.");
        }

        // Đánh dấu thành công
        otpToken.markAsUsed();
        // otpTokenRepo.save(otpToken); // Không cần thiết vì có @Transactional
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