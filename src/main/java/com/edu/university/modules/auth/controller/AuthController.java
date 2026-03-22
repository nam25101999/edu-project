package com.edu.university.modules.auth.controller;

import com.edu.university.modules.auth.dto.ResetPasswordDtos.*;
import com.edu.university.modules.auth.dto.AuthDtos.*;
import com.edu.university.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        authService.registerUser(signUpRequest);
        return ResponseEntity.ok("Đăng ký tài khoản thành công!");
    }

    // =========================================
    // API QUÊN MẬT KHẨU
    // =========================================

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.generateAndSendOtp(request);
        return ResponseEntity.ok("Mã OTP đã được gửi đến email của bạn. Mã có hiệu lực trong 5 phút.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Khôi phục mật khẩu thành công. Bạn có thể đăng nhập bằng mật khẩu mới.");
    }
}