package com.edu.university.modules.auth.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.entity.RefreshToken;
import com.edu.university.modules.auth.entity.User;
import com.edu.university.modules.auth.repository.RefreshTokenRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${spring.security.jwt.refreshExpirationMs:604800000}") // 7 ngày
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    // ================= CREATE TOKEN =================
    @Transactional
    public RefreshToken createRefreshToken(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 👉 Single device (xóa token cũ)
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .revoked(false) // 🔥 QUAN TRỌNG
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    // ================= VALIDATE TOKEN =================
    public RefreshToken validateToken(String tokenValue) {

        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "Token không hợp lệ"));

        // 🔥 Check revoked
        if (token.isRevoked()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token đã bị thu hồi");
        }

        // 🔥 Check expire
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token đã hết hạn");
        }

        return token;
    }

    // ================= LOGOUT =================
    @Transactional
    public void logout(String tokenValue) {

        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        token.setRevoked(true); // 🔥 KHÔNG delete
        refreshTokenRepository.save(token);
    }

    // ================= ROTATE TOKEN =================
    @Transactional
    public RefreshToken rotateToken(String oldTokenValue) {

        RefreshToken oldToken = validateToken(oldTokenValue);

        // 🔥 revoke token cũ
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);

        // 🔥 tạo token mới
        return createRefreshToken(oldToken.getUser().getId());
    }

    // ================= DELETE ALL (OPTIONAL) =================
    @Transactional
    public void deleteByUserId(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        refreshTokenRepository.deleteByUser(user);
    }
}