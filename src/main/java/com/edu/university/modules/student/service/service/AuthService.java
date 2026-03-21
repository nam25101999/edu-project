package com.edu.university.modules.student.service.service;

import com.edu.university.modules.auth.dto.ResetPasswordDtos.*;
import com.edu.university.modules.auth.dto.AuthDtos.*;
import com.edu.university.modules.auth.entity.OtpToken;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.User;
import com.edu.university.modules.auth.repository.OtpTokenRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.common.security.JwtUtils;
import com.edu.university.common.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edu.university.modules.report.annotation.LogAction;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    private final EmailService emailService;
    private final OtpTokenRepository otpTokenRepo;

    // =========================
    // LOGIN
    // =========================
    @LogAction(action = "LOGIN", entityName = "USER")
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), role);
    }

    // =========================
    // REGISTER
    // =========================
    @LogAction(action = "REGISTER", entityName = "USER")
    public void registerUser(SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.username())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }

        if (userRepository.existsByEmail(signUpRequest.email())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }

        User user = User.builder()
                .username(signUpRequest.username())
                .email(signUpRequest.email())
                .password(encoder.encode(signUpRequest.password()))
                .role(Role.valueOf(signUpRequest.role()))
                .build();

        userRepository.save(user);
    }

    // =========================
    // FORGOT PASSWORD (GỬI OTP)
    // =========================
    @LogAction(action = "SEND_OTP", entityName = "USER")
    @Transactional
    public void generateAndSendOtp(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy email!"));

        otpTokenRepo.deleteByUser(user);

        String otp = String.format("%06d", new Random().nextInt(999999));

        OtpToken otpToken = OtpToken.builder()
                .otp(otp)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build();

        otpTokenRepo.save(otpToken);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    // =========================
    // RESET PASSWORD
    // =========================
    @LogAction(action = "RESET_PASSWORD", entityName = "USER")
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        OtpToken otpToken = otpTokenRepo.findByOtpAndUser(request.otp(), user)
                .orElseThrow(() -> new RuntimeException("OTP không đúng!"));

        if (otpToken.isExpired()) {
            otpTokenRepo.delete(otpToken);
            throw new RuntimeException("OTP đã hết hạn!");
        }

        user.setPassword(encoder.encode(request.newPassword()));
        userRepository.save(user);

        otpTokenRepo.delete(otpToken);
    }
}