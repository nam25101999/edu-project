package com.edu.university.modules.auth.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.auth.dto.ResetPasswordDtos.*;
import com.edu.university.modules.auth.dto.AuthDtos.*;
import com.edu.university.modules.auth.service.AccountSecurityService;
import com.edu.university.modules.auth.service.AuthService;
import com.edu.university.modules.report.annotation.LogAction;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AccountSecurityService accountSecurityService;

    // ================= LOGIN =================
    @PostMapping("/login")
    @LogAction(action = "LOGIN", entityName = "AUTH")
    public ResponseEntity<ApiResponse<JwtResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(authService.authenticateUser(request))
        );
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    @LogAction(action = "REGISTER", entityName = "AUTH")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody SignupRequest request) {

        authService.registerUser(request);

        return ResponseEntity.status(201).body(
                ApiResponse.created(
                        "Đăng ký thành công! Vui lòng kiểm tra email để nhận OTP kích hoạt.",
                        null
                )
        );
    }

    // ================= LOGOUT (🔥 FIX QUAN TRỌNG) =================
    // 👉 Logout phải truyền refreshToken (không dùng userId nữa)
    @PostMapping("/logout")
    @LogAction(action = "LOGOUT", entityName = "AUTH")
    public ResponseEntity<ApiResponse<String>> logout(
            @Valid @RequestBody TokenRefreshRequest request) {

        authService.logout(request.refreshToken());

        return ResponseEntity.ok(
                ApiResponse.success("Đăng xuất thành công!")
        );
    }

    // ================= REFRESH TOKEN (🔥 ROTATION) =================
    @PostMapping("/refresh-token")
    @LogAction(action = "REFRESH_TOKEN", entityName = "AUTH")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @Valid @RequestBody TokenRefreshRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(authService.refreshToken(request))
        );
    }

    // ================= VERIFY EMAIL =================
    @PostMapping("/verify-email")
    @LogAction(action = "VERIFY_EMAIL", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<ApiResponse<String>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request) {

        accountSecurityService.verifyEmail(request);

        return ResponseEntity.ok(
                ApiResponse.success("Xác thực email thành công. Tài khoản đã kích hoạt.")
        );
    }

    // ================= RESEND OTP =================
    @PostMapping("/resend-otp")
    @LogAction(action = "RESEND_OTP", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<ApiResponse<String>> resendOtp(
            @Valid @RequestBody ResendOtpRequest request) {

        accountSecurityService.resendOtp(request);

        return ResponseEntity.ok(
                ApiResponse.success("Mã OTP mới đã được gửi.")
        );
    }

    // ================= FORGOT PASSWORD =================
    @PostMapping("/forgot-password")
    @LogAction(action = "FORGOT_PASSWORD", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        accountSecurityService.generateAndSendPasswordOtp(request);

        return ResponseEntity.ok(
                ApiResponse.success("OTP khôi phục mật khẩu đã được gửi.")
        );
    }

    // ================= RESET PASSWORD =================
    @PostMapping("/reset-password")
    @LogAction(action = "RESET_PASSWORD", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        accountSecurityService.resetPassword(request);

        return ResponseEntity.ok(
                ApiResponse.success("Khôi phục mật khẩu thành công.")
        );
    }

    // ================= CHANGE PASSWORD =================
    @PostMapping("/change-password")
    @LogAction(action = "CHANGE_PASSWORD", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {

        authService.changePassword(userDetails.getId(), request);

        return ResponseEntity.ok(
                ApiResponse.success("Thay đổi mật khẩu thành công", null)
        );
    }
}