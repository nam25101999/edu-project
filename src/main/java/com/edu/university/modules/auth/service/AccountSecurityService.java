package com.edu.university.modules.auth.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.dto.AuthDtos.ResendOtpRequest;
import com.edu.university.modules.auth.dto.AuthDtos.VerifyEmailRequest;
import com.edu.university.modules.auth.dto.ResetPasswordDtos.ForgotPasswordRequest;
import com.edu.university.modules.auth.dto.ResetPasswordDtos.ResetPasswordRequest;
import com.edu.university.modules.auth.entity.OtpToken;
import com.edu.university.modules.auth.entity.User;
import com.edu.university.modules.auth.repository.OtpTokenRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.report.annotation.LogAction;
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
    private final PasswordEncoder encoder;
    private final SecureRandom secureRandom = new SecureRandom();

    // =========================
    // 1. FORGOT & RESET PASSWORD
    // =========================

    @LogAction(action = "FORGOT_PASSWORD", entityName = "USER")
    @Transactional
    public void generateAndSendPasswordOtp(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản với email này!"));

        if (!user.isActive()) throw new BusinessException(ErrorCode.FORBIDDEN, "Tài khoản của bạn chưa được kích hoạt hoặc đã bị khóa!");
        sendOtpToUser(user);
    }

    @LogAction(action = "RESET_PASSWORD", entityName = "USER")
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản!"));

        validateAndConsumeOtp(user, request.otp());

        user.setPassword(encoder.encode(request.newPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setLocked(false);
        user.setFailedLoginAttempts(0);

        userRepository.save(user);
    }

    // =========================
    // 2. VERIFY EMAIL (ĐĂNG KÝ)
    // =========================

    @LogAction(action = "VERIFY_EMAIL", entityName = "USER")
    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản!"));

        if (user.isActive()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Tài khoản này đã được xác thực trước đó!");
        }

        validateAndConsumeOtp(user, request.otp());

        // Kích hoạt tài khoản
        user.setActive(true);
        userRepository.save(user);
    }

    // =========================
    // 3. RESEND OTP (DÙNG CHUNG)
    // =========================

    @LogAction(action = "RESEND_OTP", entityName = "USER")
    @Transactional
    public void resendOtp(ResendOtpRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản với email này!"));

        sendOtpToUser(user);
    }

    // =========================
    // PRIVATE HELPER METHODS
    // =========================

    public void sendOtpToUser(User user) {
        otpTokenRepo.deleteByUser(user); // Xóa OTP cũ

        String otp = String.format("%06d", secureRandom.nextInt(1000000));
        OtpToken otpToken = OtpToken.builder()
                .otp(otp)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build();
        otpTokenRepo.save(otpToken);

        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            log.error("Lỗi khi gửi OTP qua Email: ", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Không thể gửi email OTP. Vui lòng thử lại sau!");
        }
    }

    private void validateAndConsumeOtp(User user, String incomingOtp) {
        OtpToken otpToken = otpTokenRepo.findByOtpAndUser(incomingOtp, user)
                .orElseThrow(() -> new BusinessException(ErrorCode.OTP_INVALID, "Mã OTP không chính xác!"));

        if (otpToken.isExpired()) {
            otpTokenRepo.delete(otpToken);
            throw new BusinessException(ErrorCode.OTP_EXPIRED, "Mã OTP đã hết hạn!");
        }
        otpTokenRepo.delete(otpToken); // Xóa OTP sau khi dùng
    }
}