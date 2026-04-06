package com.edu.university.modules.auth.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.auth.dto.AuthDtos;
import com.edu.university.modules.auth.dto.ResetPasswordDtos.*;
import com.edu.university.modules.auth.dto.AuthDtos.*;
import com.edu.university.modules.auth.dto.UserResponseDTO;
import com.edu.university.modules.auth.service.AccountSecurityService;
import com.edu.university.modules.auth.service.AuthService;
import com.edu.university.modules.auth.annotation.LogAction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<BaseResponse<JwtResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        // DÃ¹ng trá»±c tiáº¿p hÃ m success tÄ©nh, vá»«a ngáº¯n gá»n vá»«a trÃ¡nh lá»—i Maven/Lombok
        return ResponseEntity.ok(
                BaseResponse.ok("Login successful", authService.authenticateUser(request))
        );
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    @LogAction(action = "REGISTER", entityName = "AUTH")
    public ResponseEntity<BaseResponse<String>> register(
            @Valid @RequestBody SignupRequest request) {

        authService.registerUser(request);

        return ResponseEntity.status(201).body(
                BaseResponse.created(
                        "ÄÄƒng kÃ½ thÃ nh cÃ´ng! Vui lÃ²ng kiá»ƒm tra email Ä‘á»ƒ nháº­n OTP kÃ­ch hoáº¡t.",
                        null
                )
        );
    }

    // ================= LOGOUT (ðŸ”¥ FIX QUAN TRá»ŒNG) =================
    @PostMapping("/logout")
    // @LogAction á»Ÿ Ä‘Ã¢y cÅ©ng Ä‘Æ°á»£c, nhÆ°ng thÆ°á»ng Ä‘áº·t á»Ÿ Service lÃ  Ä‘á»§ Ä‘á»ƒ track nghiá»‡p vá»¥
    public ResponseEntity<BaseResponse<String>> logout(
            @Valid @RequestBody AuthDtos.TokenRefreshRequest request) {

        // Gá»i service Ä‘á»ƒ xá»­ lÃ½ thu há»“i token trong DB
        authService.logout(request.refreshToken());

        // Sau khi Service (vÃ  LogAction) hoÃ n táº¥t, ta cÃ³ thá»ƒ chá»§ Ä‘á»™ng xÃ³a context á»Ÿ Ä‘Ã¢y náº¿u muá»‘n an toÃ n tuyá»‡t Ä‘á»‘i
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(
                BaseResponse.ok("ÄÄƒng xuáº¥t thÃ nh cÃ´ng!", null)
        );
    }

    // ================= REFRESH TOKEN (ðŸ”¥ ROTATION) =================
    @PostMapping("/refresh-token")
    @LogAction(action = "REFRESH_TOKEN", entityName = "AUTH")
    public ResponseEntity<BaseResponse<TokenRefreshResponse>> refreshToken(
            @Valid @RequestBody TokenRefreshRequest request) {

        return ResponseEntity.ok(
                BaseResponse.ok(authService.refreshToken(request))
        );
    }

    // ================= VERIFY EMAIL =================
    @PostMapping("/verify-email")
    @LogAction(action = "VERIFY_EMAIL", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<BaseResponse<String>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request) {

        accountSecurityService.verifyEmail(request);

        return ResponseEntity.ok(
                BaseResponse.ok("XÃ¡c thá»±c email thÃ nh cÃ´ng. TÃ i khoáº£n Ä‘Ã£ kÃ­ch hoáº¡t.", null)
        );
    }

    // ================= RESEND OTP =================
    @PostMapping("/resend-otp")
    @LogAction(action = "RESEND_OTP", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<BaseResponse<String>> resendOtp(
            @Valid @RequestBody ResendOtpRequest request) {

        accountSecurityService.resendOtp(request);

        return ResponseEntity.ok(
                BaseResponse.ok("MÃ£ OTP má»›i Ä‘Ã£ Ä‘Æ°á»£c gá»­i.", null)
        );
    }

    // ================= FORGOT PASSWORD =================
    @PostMapping("/forgot-password")
    @LogAction(action = "FORGOT_PASSWORD", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<BaseResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        accountSecurityService.generateAndSendPasswordOtp(request);

        return ResponseEntity.ok(
                BaseResponse.ok("OTP khÃ´i phá»¥c máº­t kháº©u Ä‘Ã£ Ä‘Æ°á»£c gá»­i.", null)
        );
    }

    // ================= RESET PASSWORD =================
    @PostMapping("/reset-password")
    @LogAction(action = "RESET_PASSWORD", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<BaseResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        accountSecurityService.resetPassword(request);

        return ResponseEntity.ok(
                BaseResponse.ok("KhÃ´i phá»¥c máº­t kháº©u thÃ nh cÃ´ng.", null)
        );
    }

    // ================= CHANGE PASSWORD =================
    @PostMapping("/change-password")
    @LogAction(action = "CHANGE_PASSWORD", entityName = "ACCOUNT_SECURITY")
    public ResponseEntity<BaseResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {

        authService.changePassword(userDetails.getId(), request);

        return ResponseEntity.ok(
                BaseResponse.ok("Thay Ä‘á»•i máº­t kháº©u thÃ nh cÃ´ng", null)
        );
    }

    // ================= CRUD USERS (ADMIN/MANAGER) =================

    @GetMapping("/users")
    @LogAction(action = "READ_ALL", entityName = "USER")
    public ResponseEntity<BaseResponse<Page<UserResponseDTO>>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(
                BaseResponse.ok("Láº¥y danh sÃ¡ch ngÆ°á»i dÃ¹ng thÃ nh cÃ´ng", authService.getAllUsers(pageable))
        );
    }

    @GetMapping("/users/{id}")
    @LogAction(action = "READ", entityName = "USER")
    public ResponseEntity<BaseResponse<UserResponseDTO>> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                BaseResponse.ok("Láº¥y thÃ´ng tin ngÆ°á»i dÃ¹ng thÃ nh cÃ´ng", authService.getUserById(id))
        );
    }

    @PostMapping("/users")
    @LogAction(action = "CREATE", entityName = "USER")
    public ResponseEntity<BaseResponse<UserResponseDTO>> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponseDTO newUser = authService.createUser(
                request.username(),
                request.email(),
                request.password(),
                request.roles(),
                request.isActive()
        );
        return ResponseEntity.status(201).body(
                BaseResponse.created("Táº¡o ngÆ°á»i dÃ¹ng thÃ nh cÃ´ng", newUser)
        );
    }

    @PutMapping("/users/{id}")
    @LogAction(action = "UPDATE", entityName = "USER")
    public ResponseEntity<BaseResponse<UserResponseDTO>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponseDTO updatedUser = authService.updateUser(
                id,
                request.email(),
                request.isActive(),
                request.roles()
        );
        return ResponseEntity.ok(
                BaseResponse.ok("Cáº­p nháº­t thÃ´ng tin ngÆ°á»i dÃ¹ng thÃ nh cÃ´ng", updatedUser)
        );
    }

    @DeleteMapping("/users/{id}")
    @LogAction(action = "DELETE", entityName = "USER")
    public ResponseEntity<BaseResponse<Void>> deleteUser(@PathVariable UUID id) {
        authService.deleteUser(id);
        return ResponseEntity.ok(
                BaseResponse.ok("XÃ³a ngÆ°á»i dÃ¹ng thÃ nh cÃ´ng", null)
        );
    }
}