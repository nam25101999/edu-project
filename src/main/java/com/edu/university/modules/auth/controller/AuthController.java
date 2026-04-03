package com.edu.university.modules.auth.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.auth.dto.AuthDtos;
import com.edu.university.modules.auth.dto.ResetPasswordDtos.*;
import com.edu.university.modules.auth.dto.AuthDtos.*;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.service.AccountSecurityService;
import com.edu.university.modules.auth.service.AuthService;
import com.edu.university.modules.auth.annotation.LogAction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.UUID;

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

        // Dùng trực tiếp hàm success tĩnh, vừa ngắn gọn vừa tránh lỗi Maven/Lombok
        return ResponseEntity.ok(
                ApiResponse.success("Login successful", authService.authenticateUser(request))
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
    @PostMapping("/logout")
    // @LogAction ở đây cũng được, nhưng thường đặt ở Service là đủ để track nghiệp vụ
    public ResponseEntity<ApiResponse<String>> logout(
            @Valid @RequestBody AuthDtos.TokenRefreshRequest request) {

        // Gọi service để xử lý thu hồi token trong DB
        authService.logout(request.refreshToken());

        // Sau khi Service (và LogAction) hoàn tất, ta có thể chủ động xóa context ở đây nếu muốn an toàn tuyệt đối
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(
                ApiResponse.success("Đăng xuất thành công!", null)
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
                ApiResponse.success("Xác thực email thành công. Tài khoản đã kích hoạt.", null)
        );
    }

    // ================= RESEND OTP =================
    @PostMapping("/resend-otp")
    @LogAction(action = "RESEND_OTP", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<ApiResponse<String>> resendOtp(
            @Valid @RequestBody ResendOtpRequest request) {

        accountSecurityService.resendOtp(request);

        return ResponseEntity.ok(
                ApiResponse.success("Mã OTP mới đã được gửi.", null)
        );
    }

    // ================= FORGOT PASSWORD =================
    @PostMapping("/forgot-password")
    @LogAction(action = "FORGOT_PASSWORD", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        accountSecurityService.generateAndSendPasswordOtp(request);

        return ResponseEntity.ok(
                ApiResponse.success("OTP khôi phục mật khẩu đã được gửi.", null)
        );
    }

    // ================= RESET PASSWORD =================
    @PostMapping("/reset-password")
    @LogAction(action = "RESET_PASSWORD", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        accountSecurityService.resetPassword(request);

        return ResponseEntity.ok(
                ApiResponse.success("Khôi phục mật khẩu thành công.", null)
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

    // ================= CRUD USERS (ADMIN/MANAGER) =================

    @GetMapping("/users")
    @LogAction(action = "READ_ALL", entityName = "USER")
    public ResponseEntity<ApiResponse<List<Users>>> getAllUsers() {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy danh sách người dùng thành công", authService.getAllUsers())
        );
    }

    @GetMapping("/users/{id}")
    @LogAction(action = "READ", entityName = "USER")
    public ResponseEntity<ApiResponse<Users>> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy thông tin người dùng thành công", authService.getUserById(id))
        );
    }

    @PostMapping("/users")
    @LogAction(action = "CREATE", entityName = "USER")
    public ResponseEntity<ApiResponse<Users>> createUser(@Valid @RequestBody UserCreateRequest request) {
        Users newUser = authService.createUser(
                request.username(),
                request.email(),
                request.password(),
                request.roles(),
                request.isActive()
        );
        return ResponseEntity.status(201).body(
                ApiResponse.created("Tạo người dùng thành công", newUser)
        );
    }

    @PutMapping("/users/{id}")
    @LogAction(action = "UPDATE", entityName = "USER")
    public ResponseEntity<ApiResponse<Users>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request) {
        Users updatedUser = authService.updateUser(
                id,
                request.email(),
                request.isActive(),
                request.roles()
        );
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật thông tin người dùng thành công", updatedUser)
        );
    }

    @DeleteMapping("/users/{id}")
    @LogAction(action = "DELETE", entityName = "USER")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        authService.deleteUser(id);
        return ResponseEntity.ok(
                ApiResponse.success("Xóa người dùng thành công", null)
        );
    }
}