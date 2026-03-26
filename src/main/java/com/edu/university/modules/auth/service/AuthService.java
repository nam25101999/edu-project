package com.edu.university.modules.auth.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.common.security.JwtUtils;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.auth.dto.AuthDtos.*;
import com.edu.university.modules.auth.entity.RefreshToken;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.User;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.report.annotation.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edu.university.modules.auth.dto.AuthDtos.TokenRefreshRequest;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    // ================= LOGIN =================
    @Transactional
    @LogAction(action = "LOGIN", entityName = "USER")
    public JwtResponse authenticateUser(LoginRequest request) {

        // 1. Lấy giá trị đầu vào (có thể là username, email, hoặc mã SV)
        String identifier = request.identifier();

        // 2. Tìm user trong DB bằng chuỗi identifier này
        // Yêu cầu: Bạn cần thêm method findByIdentifier trong UserRepository
        User user = userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 3. Xác thực qua Spring Security bằng username chuẩn lấy từ DB
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(), // Luôn truyền username thực tế để Spring Security xử lý
                        request.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // 4. Cập nhật thông tin thống kê đăng nhập của User
        // Chúng ta tái sử dụng luôn entity user đã tìm ở bước 2 thay vì gọi lại userRepository.findById()
        user.setLastLoginAt(LocalDateTime.now());
        user.setFailedLoginAttempts(0); // Reset số lần đăng nhập sai
        userRepository.save(user);

        // 5. Tạo Tokens
        String accessToken = jwtUtils.generateJwtToken(authentication);
        RefreshToken refreshToken = tokenService.createRefreshToken(userDetails.getId());

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(item -> item.getAuthority())
                .orElse(Role.ROLE_STUDENT.name());

        return new JwtResponse(
                accessToken,
                refreshToken.getToken(),
                userDetails.getId(),
                userDetails.getUsername(),
                role
        );
    }

    // ================= REGISTER =================
    @Transactional
    @LogAction(action = "SIGNUP", entityName = "USER")
    public User registerUser(SignupRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // Khởi tạo User mới, các trường boolean mặc định đã được xử lý bởi @Builder.Default trong Entity
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_STUDENT) // Gán Role mặc định
                .build();

        return userRepository.save(user);
    }

    // ================= LOGOUT =================
    @Transactional
    @LogAction(action = "LOGOUT", entityName = "USER")
    public void logout(String refreshToken) {
        tokenService.logout(refreshToken);
    }

    // ================= CHANGE PASSWORD =================
    @Transactional
    @LogAction(action = "CHANGE_PASSWORD", entityName = "USER")
    public void changePassword(UUID userId, ChangePasswordRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // check mật khẩu cũ
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        // không cho trùng mật khẩu cũ
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        // Cập nhật mật khẩu và lưu vết thời gian đổi
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    // ================= REFRESH TOKEN =================
    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {

        RefreshToken newToken = tokenService.rotateToken(request.refreshToken());

        String newAccessToken = jwtUtils.generateTokenFromUsername(
                newToken.getUser().getUsername()
        );

        return new TokenRefreshResponse(
                newAccessToken,
                newToken.getToken()
        );
    }
}