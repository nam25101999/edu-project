package com.edu.university.modules.auth.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.auth.dto.AuthDtos;
import com.edu.university.modules.auth.dto.ResetPasswordDtos.*;
import com.edu.university.modules.auth.dto.AuthDtos.*;
import com.edu.university.modules.auth.dto.UserResponseDTO;
import com.edu.university.modules.auth.service.AccountSecurityService;
import com.edu.university.modules.auth.service.AuthService;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.auth.annotation.LogAction;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AccountSecurityService accountSecurityService;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // ================= CURRENT USER (ME) =================
    @GetMapping("/me")
    @LogAction(action = "GET_PROFILE", entityName = "USER")
    public ResponseEntity<BaseResponse<AuthDtos.MeResponse>> getCurrentUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        String cacheKey = "user:profile:" + userDetails.getId();
        
        // 1. Try to get from Cache
        AuthDtos.MeResponse cachedProfile = (AuthDtos.MeResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cachedProfile != null) {
            return ResponseEntity.ok(BaseResponse.ok("Lấy profile từ cache thành công", cachedProfile));
        }

        // 2. Fallback to UserDetails (Principal)
        AuthDtos.MeResponse profile = new AuthDtos.MeResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getRoles(),
                userDetails.isActive(),
                userDetails.isEmailVerified(),
                userDetails.getLastLoginAt(),
                authService.getStudentProfile(userDetails.getId())
        );

        // 3. Save to Cache (5 minutes)
        redisTemplate.opsForValue().set(cacheKey, profile, 5, TimeUnit.MINUTES);

        return ResponseEntity.ok(BaseResponse.ok("Lấy thông tin cá nhân thành công", profile));
    }

    @PutMapping("/profile")
    @LogAction(action = "UPDATE_PROFILE", entityName = "USER")
    public ResponseEntity<BaseResponse<String>> updateProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody AuthDtos.ProfileUpdateRequest request) {
        
        authService.updateProfile(userDetails.getId(), request);
        
        // Invalidate Profile Cache
        redisTemplate.delete("user:profile:" + userDetails.getId());
        
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật hồ sơ thành công", null));
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    @LogAction(action = "LOGIN", entityName = "AUTH")
    public ResponseEntity<BaseResponse<AuthDtos.JwtResponse>> login(
            @Valid @RequestBody AuthDtos.LoginRequest request) {

        return ResponseEntity.ok(
                BaseResponse.ok("Đăng nhập thành công", authService.authenticateUser(request))
        );
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    @LogAction(action = "REGISTER", entityName = "AUTH")
    public ResponseEntity<BaseResponse<String>> register(
            @Valid @RequestBody AuthDtos.SignupRequest request) {

        authService.registerUser(request);

        return ResponseEntity.status(201).body(
                BaseResponse.created(
                        "Đăng ký thành công! Vui lòng kiểm tra email để nhận OTP kích hoạt.",
                        null
                )
        );
    }

    // ================= LOGOUT =================
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<String>> logout(
            @Valid @RequestBody AuthDtos.TokenRefreshRequest request) {

        authService.logout(request.refreshToken());
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(
                BaseResponse.ok("Đăng xuất thành công!", null)
        );
    }

    // ================= REFRESH TOKEN =================
    @PostMapping("/refresh-token")
    @LogAction(action = "REFRESH_TOKEN", entityName = "AUTH")
    public ResponseEntity<BaseResponse<AuthDtos.TokenRefreshResponse>> refreshToken(
            @Valid @RequestBody AuthDtos.TokenRefreshRequest request) {

        return ResponseEntity.ok(
                BaseResponse.ok(authService.refreshToken(request))
        );
    }

    // ================= VERIFY EMAIL =================
    @PostMapping("/verify-email")
    @LogAction(action = "VERIFY_EMAIL", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<BaseResponse<String>> verifyEmail(
            @Valid @RequestBody AuthDtos.VerifyEmailRequest request) {

        accountSecurityService.verifyEmail(request);

        return ResponseEntity.ok(
                BaseResponse.ok("Xác thực email thành công. Tài khoản đã kích hoạt.", null)
        );
    }

    // ================= RESEND OTP =================
    @PostMapping("/resend-otp")
    @LogAction(action = "RESEND_OTP", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<BaseResponse<String>> resendOtp(
            @Valid @RequestBody AuthDtos.ResendOtpRequest request) {

        accountSecurityService.resendOtp(request);

        return ResponseEntity.ok(
                BaseResponse.ok("Mã OTP mới đã được gửi.", null)
        );
    }

    // ================= FORGOT PASSWORD =================
    @PostMapping("/forgot-password")
    @LogAction(action = "FORGOT_PASSWORD", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<BaseResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        accountSecurityService.generateAndSendPasswordOtp(request);

        return ResponseEntity.ok(
                BaseResponse.ok("OTP khôi phục mật khẩu đã được gửi.", null)
        );
    }

    @PostMapping("/verify-reset-otp")
    @LogAction(action = "VERIFY_RESET_OTP", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<BaseResponse<String>> verifyResetOtp(
            @Valid @RequestBody VerifyResetOtpRequest request) {

        accountSecurityService.verifyResetOtp(request.email(), request.otp());

        return ResponseEntity.ok(
                BaseResponse.ok("Mã OTP hợp lệ.", null)
        );
    }

    // ================= RESET PASSWORD =================
    @PostMapping("/reset-password")
    @LogAction(action = "RESET_PASSWORD", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<BaseResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        accountSecurityService.resetPassword(request);
        
        // Invalidate Cache
        Users user = userRepository.findByEmail(request.email()).orElse(null);
        if (user != null) redisTemplate.delete("user:profile:" + user.getId());

        return ResponseEntity.ok(
                BaseResponse.ok("Khôi phục mật khẩu thành công.", null)
        );
    }

    // ================= CHANGE PASSWORD =================
    @PostMapping("/change-password")
    @LogAction(action = "CHANGE_PASSWORD", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<BaseResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody AuthDtos.ChangePasswordRequest request) {

        authService.changePassword(userDetails.getId(), request);
        
        // Invalidate Cache
        redisTemplate.delete("user:profile:" + userDetails.getId());

        return ResponseEntity.ok(
                BaseResponse.ok("Thay đổi mật khẩu thành công", null)
        );
    }

    // ================= CRUD USERS (ADMIN/MANAGER) =================

    @GetMapping("/users")
    @LogAction(action = "READ_ALL", entityName = "USER")
    public ResponseEntity<BaseResponse<PageResponse<UserResponseDTO>>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(
                BaseResponse.okPage(authService.getAllUsers(pageable))
        );
    }

    @GetMapping("/users/staff")
    @LogAction(action = "READ_STAFF", entityName = "USER")
    public ResponseEntity<BaseResponse<PageResponse<UserResponseDTO>>> getStaffUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(
                BaseResponse.okPage(authService.getStaffUsers(pageable))
        );
    }

    @GetMapping("/users/{id}")
    @LogAction(action = "READ", entityName = "USER")
    public ResponseEntity<BaseResponse<UserResponseDTO>> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.ok("Lấy thông tin người dùng thành công", authService.getUserById(id))
        );
    }

    @PostMapping("/users")
    @LogAction(action = "CREATE", entityName = "USER")
    public ResponseEntity<BaseResponse<UserResponseDTO>> createUser(@Valid @RequestBody AuthDtos.UserCreateRequest request) {
        UserResponseDTO newUser = authService.createUser(
                request.username(),
                request.email(),
                request.password(),
                request.roles(),
                request.isActive()
        );
        return ResponseEntity.status(201).body(
                BaseResponse.created("Tạo người dùng thành công", newUser)
        );
    }

    @PutMapping("/users/{id}")
    @LogAction(action = "UPDATE", entityName = "USER")
    public ResponseEntity<BaseResponse<UserResponseDTO>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody AuthDtos.UserUpdateRequest request) {
        UserResponseDTO updatedUser = authService.updateUser(
                id,
                request.email(),
                request.isActive(),
                request.roles()
        );
        
        // Invalidate Cache
        redisTemplate.delete("user:profile:" + id);

        return ResponseEntity.ok(
                BaseResponse.ok("Cập nhật thông tin người dùng thành công", updatedUser)
        );
    }

    @DeleteMapping("/users/{id}")
    @LogAction(action = "DELETE", entityName = "USER")
    public ResponseEntity<BaseResponse<Void>> deleteUser(@PathVariable UUID id) {
        authService.deleteUser(id);
        return ResponseEntity.ok(
                BaseResponse.ok("Xóa người dùng thành công", null)
        );
    }
}