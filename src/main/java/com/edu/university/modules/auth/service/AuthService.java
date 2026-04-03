package com.edu.university.modules.auth.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.common.security.JwtUtils;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.auth.dto.AuthDtos;
import com.edu.university.modules.auth.dto.AuthDtos.*;
import com.edu.university.modules.auth.entity.RefreshToken;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.auth.annotation.LogAction;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edu.university.modules.auth.repository.RoleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final HttpServletRequest httpRequest;

    // ================= LOGIN =================
    @Transactional
    @LogAction(action = "LOGIN", entityName = "USER")
    public JwtResponse authenticateUser(LoginRequest request) {

        Users user = userRepository.findByIdentifier(request.identifier())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 1. Chống Brute Force: Kiểm tra xem tài khoản có đang bị khóa không
        if (user.getLockUntil() != null && user.getLockUntil().isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Tài khoản đang bị khóa tạm thời do nhập sai quá nhiều lần. Vui lòng thử lại sau.");
        }

        Authentication authentication;
        try {
            // 2. Xác thực Spring Security
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), request.password())
            );
        } catch (BadCredentialsException e) {
            handleFailedLogin(user);
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "Tài khoản hoặc mật khẩu không chính xác.");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // 3. Reset bộ đếm lỗi & Cập nhật Last Login
        user.setLastLoginAt(LocalDateTime.now());
        user.setFailedLoginAttempts(0);
        user.setLockUntil(null);
        user.setLastLoginIp(getClientIp());
        user.setLastLoginUserAgent(getUserAgent());
        userRepository.save(user);

        // 4. Sinh Tokens (Quản lý thiết bị qua TokenService)
        String accessToken = jwtUtils.generateJwtToken(authentication);

        // Cấp Refresh Token mới (Sinh ra 1 Family ID mới cho thiết bị này)
        RefreshToken refreshToken = tokenService.createRefreshToken(user, getClientIp(), getUserAgent(), UUID.randomUUID().toString());

        // Lấy danh sách tất cả các Role của User thay vì chỉ lấy 1
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .toList();

        // Khởi tạo object UserInfo
        AuthDtos.JwtResponse.UserInfo userInfo = new AuthDtos.JwtResponse.UserInfo(
                userDetails.getId(),
                userDetails.getUsername(),
                roles
        );

        // Trả về cấu trúc JSON chuẩn
        return new JwtResponse(
                accessToken,
                refreshToken.getTokenPlain(),
                "Bearer",
                3600L, // Thời gian hết hạn tính bằng giây (3600s = 1 giờ)
                userInfo
        );
    }


    // ================= REGISTER =================
    @Transactional
    @LogAction(action = "SIGNUP", entityName = "USER")
    public Users registerUser(SignupRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Users user = Users.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .isActive(false) // Mới đăng ký thì chưa active, phải Verify Email
                .emailVerified(false)
                .build();

        // Lấy Role từ DB và gán cho User lúc đăng ký
        Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Lỗi hệ thống: Chưa cấu hình Role mặc định."));

        user.getRoles().add(studentRole);

        return userRepository.save(user);
    }

    // ================= LOGOUT =================
    @Transactional
    @LogAction(action = "LOGOUT", entityName = "USER")
    public void logout(String plainRefreshToken) {
        // 1. Vô hiệu hóa token trong Database (Chuyển is_revoked = true)
        tokenService.logout(plainRefreshToken);
    }

    // ================= LOGOUT ALL DEVICES =================
    @Transactional
    @LogAction(action = "LOGOUT_ALL", entityName = "USER")
    public void logoutAllDevices(UUID userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.incrementTokenVersion(); // Kick toàn bộ Access Token (Cần check version trong JwtFilter)
        userRepository.save(user);

        tokenService.revokeAllUserTokens(user); // Thu hồi toàn bộ Refresh Token
    }

    // ================= CHANGE PASSWORD =================
    @Transactional
    @LogAction(action = "CHANGE_PASSWORD", entityName = "USER")
    public void changePassword(UUID userId, ChangePasswordRequest request) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "Mật khẩu cũ không chính xác!");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Mật khẩu mới không được trùng mật khẩu cũ!");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.incrementTokenVersion(); // Buộc đăng nhập lại trên toàn bộ thiết bị

        userRepository.save(user);
        tokenService.revokeAllUserTokens(user);
    }

    // ================= REFRESH TOKEN =================
    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {

        RefreshToken newRtEntity = tokenService.rotateToken(request.refreshToken(), getClientIp(), getUserAgent());

        String newAccessToken = jwtUtils.generateTokenFromUsername(
                newRtEntity.getUser().getUsername()
        );

        return new TokenRefreshResponse(
                newAccessToken,
                newRtEntity.getTokenPlain() // Plain text
        );
    }

    // ================= CRUD OPERATIONS FOR USER =================

    // 1. GET ALL USERS
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    // 2. GET USER BY ID
    public Users getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng với ID này"));
    }

    // 3. CREATE USER (Dành cho Admin - Có thể gán Roles tuỳ chỉnh)
    @Transactional
    @LogAction(action = "CREATE", entityName = "USER")
    public Users createUser(String username, String email, String password, List<String> roleNames, boolean isActive) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Users user = Users.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .isActive(isActive)
                .emailVerified(isActive)
                .build();

        // Gán Role
        if (roleNames != null && !roleNames.isEmpty()) {
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Role không tồn tại: " + roleName));
                user.getRoles().add(role);
            }
        } else {
            // Role mặc định nếu Admin không chọn
            Role defaultRole = roleRepository.findByName("ROLE_STUDENT")
                    .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Chưa cấu hình Role mặc định."));
            user.getRoles().add(defaultRole);
        }

        return userRepository.save(user);
    }

    // 4. UPDATE USER
    @Transactional
    @LogAction(action = "UPDATE", entityName = "USER")
    public Users updateUser(UUID id, String email, Boolean isActive, List<String> roleNames) {
        Users user = getUserById(id);

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(email);
        }

        if (isActive != null) {
            user.setActive(isActive);
            if (!isActive) {
                // Nếu khóa tài khoản, ép đăng xuất thiết bị hiện tại
                user.incrementTokenVersion();
                tokenService.revokeAllUserTokens(user);
            }
        }

        // Cập nhật Roles
        if (roleNames != null) {
            user.getRoles().clear();
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Role không tồn tại: " + roleName));
                user.getRoles().add(role);
            }
        }

        return userRepository.save(user);
    }

    // 5. DELETE (Xoá mềm hoặc khoá)
    @Transactional
    @LogAction(action = "DELETE", entityName = "USER")
    public void deleteUser(UUID id) {
        Users user = getUserById(id);

        // Phương pháp tốt nhất cho User là Xóa mềm (Soft Delete)
        user.setActive(false);
        // user.setDeletedAt(LocalDateTime.now()); // Mở comment nếu Entity Users của bạn hỗ trợ Audit deletedAt

        userRepository.save(user);

        // Buộc người dùng bị xóa văng khỏi mọi thiết bị
        user.incrementTokenVersion();
        tokenService.revokeAllUserTokens(user);
    }


    // ================= PRIVATE METHODS =================
    private void handleFailedLogin(Users user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        user.setLastFailedLoginAt(LocalDateTime.now());

        if (user.getFailedLoginAttempts() >= 5) {
            user.setLockUntil(LocalDateTime.now().plusMinutes(15)); // Khóa 15 phút
            log.warn("Tài khoản {} đã bị khóa 15 phút do Brute Force", user.getUsername());
        }
        userRepository.save(user);
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