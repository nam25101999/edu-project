package com.edu.university.service;

import com.edu.university.dto.PayloadDtos.*;
import com.edu.university.dto.ResetPasswordDtos.*;
import com.edu.university.entity.OtpToken;
import com.edu.university.entity.Role;
import com.edu.university.entity.User;
import com.edu.university.repository.OtpTokenRepository;
import com.edu.university.repository.UserRepository;
import com.edu.university.security.JwtUtils;
import com.edu.university.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    // Thêm các dependency cho phần Quên mật khẩu
    private final EmailService emailService;
    private final OtpTokenRepository otpTokenRepo;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst().orElse("");

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), role);
    }

    public void registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.username())) {
            throw new RuntimeException("Lỗi: Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(signUpRequest.email())) {
            throw new RuntimeException("Lỗi: Email đã được sử dụng!");
        }

        User user = User.builder()
                .username(signUpRequest.username())
                .email(signUpRequest.email())
                .password(encoder.encode(signUpRequest.password()))
                .role(Role.valueOf(signUpRequest.role()))
                .build();

        userRepository.save(user);
    }

    // =========================================
    // TÍNH NĂNG QUÊN MẬT KHẨU
    // =========================================

    @Transactional
    public void generateAndSendOtp(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email()) // Cần đảm bảo UserRepository có hàm findByEmail
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email này!"));

        // Xóa OTP cũ nếu có
        otpTokenRepo.deleteByUser(user);

        // Tạo OTP 6 số ngẫu nhiên
        String otp = String.format("%06d", new Random().nextInt(999999));

        OtpToken otpToken = OtpToken.builder()
                .otp(otp)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(5)) // Hết hạn sau 5 phút
                .build();

        otpTokenRepo.save(otpToken);

        // Gửi qua Email
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email này!"));

        OtpToken otpToken = otpTokenRepo.findByOtpAndUser(request.otp(), user)
                .orElseThrow(() -> new RuntimeException("Mã OTP không chính xác!"));

        if (otpToken.isExpired()) {
            otpTokenRepo.delete(otpToken);
            throw new RuntimeException("Mã OTP đã hết hạn, vui lòng yêu cầu lại!");
        }

        // Đổi mật khẩu
        user.setPassword(encoder.encode(request.newPassword()));
        userRepository.save(user);

        // Xóa OTP sau khi dùng thành công
        otpTokenRepo.delete(otpToken);
    }
}