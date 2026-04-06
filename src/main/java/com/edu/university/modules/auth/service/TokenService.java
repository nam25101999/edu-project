package com.edu.university.modules.auth.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.entity.RefreshToken;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

/**
 * Token Service - PhiÃªn báº£n Security Level 10 (Optimized)
 * Ãp dá»¥ng: Hash Storage, Token Rotation, Reuse Detection (Family Token), SecureRandom Generation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${spring.security.jwt.refreshExpirationMs:604800000}") // 7 ngÃ y
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    // Sá»­ dá»¥ng SecureRandom thay vÃ¬ UUID Ä‘á»ƒ sinh token an toÃ n tuyá»‡t Ä‘á»‘i vá» máº·t máº­t mÃ£ há»c
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

    // ================= CREATE TOKEN (MULTI-DEVICE) =================
    @Transactional
    public RefreshToken createRefreshToken(Users user, String ipAddress, String userAgent, String familyId) {

        // 1. Sinh chuá»—i Token ngáº«u nhiÃªn chuáº©n Cryptography
        String plainToken = generateSecureToken();

        // 2. Hash Token Ä‘á»ƒ lÆ°u vÃ o Database
        String hashedToken = hashToken(plainToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hashedToken)
                .familyId(familyId)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .isRevoked(false)
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        // Truyá»n Plain Token qua Transient field Ä‘á»ƒ tráº£ vá» cho Client
        savedToken.setTokenPlain(plainToken);
        return savedToken;
    }

    // ================= ROTATE TOKEN (REUSE DETECTION) =================
    @Transactional
    public RefreshToken rotateToken(String oldPlainToken, String ipAddress, String userAgent) {

        if (!StringUtils.hasText(oldPlainToken)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng.");
        }

        String hashedOldToken = hashToken(oldPlainToken);

        RefreshToken oldToken = refreshTokenRepository.findByTokenHash(hashedOldToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "Refresh Token khÃ´ng há»£p lá»‡ hoáº·c khÃ´ng tá»“n táº¡i."));

        // ðŸ”¥ PHÃT HIá»†N REUSE (TOKEN Bá»Š ÄÃNH Cáº®P VÃ€ Sá»¬ Dá»¤NG Láº I)
        if (oldToken.isRevoked()) {
            log.warn("ðŸš¨ Cáº¢NH BÃO Báº¢O Máº¬T: PhÃ¡t hiá»‡n sá»­ dá»¥ng láº¡i Token cÅ©! User: {} | Family: {} | IP: {}",
                    oldToken.getUser().getUsername(), oldToken.getFamilyId(), ipAddress);

            // THU Há»’I TOÃ€N Bá»˜ FAMILY (Cáº¯t Ä‘á»©ng phiÃªn Ä‘Äƒng nháº­p trÃªn thiáº¿t bá»‹ Ä‘Ã³)
            revokeFamilyTokens(oldToken.getFamilyId());
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "PhÃ¡t hiá»‡n hÃ nh vi báº¥t thÆ°á»ng. PhiÃªn Ä‘Äƒng nháº­p Ä‘Ã£ bá»‹ vÃ´ hiá»‡u hÃ³a Ä‘á»ƒ báº£o vá»‡ tÃ i khoáº£n!");
        }

        // ðŸ”¥ Kiá»ƒm tra háº¿t háº¡n (KhÃ´ng Ä‘Æ°á»£c xÃ³a token á»Ÿ Ä‘Ã¢y Ä‘á»ƒ giá»¯ Audit Trail cho Reuse Detection)
        if (oldToken.isExpired()) {
            oldToken.setRevoked(true);
            // KhÃ´ng xÃ³a (delete) oldToken, chá»‰ Ä‘Ã¡nh dáº¥u revoked Ä‘á»ƒ náº¿u hacker dÃ¹ng láº¡i token háº¿t háº¡n nÃ y ta váº«n track Ä‘Æ°á»£c family
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "PhiÃªn Ä‘Äƒng nháº­p Ä‘Ã£ háº¿t háº¡n. Vui lÃ²ng Ä‘Äƒng nháº­p láº¡i.");
        }

        // 1. Sinh token má»›i trÆ°á»›c
        String newPlainToken = generateSecureToken();
        String newHashedToken = hashToken(newPlainToken);

        // 2. Revoke token cÅ©, cáº­p nháº­t thÃ´ng tin
        // LÆ°u Ã½: Giáº£ Ä‘á»‹nh entity RefreshToken cá»§a báº¡n cÃ³ hÃ m revoke() nháº­n vÃ o hash cá»§a token káº¿ nhiá»‡m
        oldToken.revoke(newHashedToken);
        oldToken.markAsUsed();
        // refreshTokenRepository.save(oldToken); // KhÃ´ng cáº§n thiáº¿t vÃ¬ oldToken lÃ  Managed Entity trong @Transactional

        // 3. Táº¡o token má»›i Káº¾ THá»ªA FAMILY ID CÅ¨
        RefreshToken newToken = RefreshToken.builder()
                .user(oldToken.getUser())
                .tokenHash(newHashedToken)
                .familyId(oldToken.getFamilyId()) // Giá»¯ nguyÃªn Family
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .isRevoked(false)
                .build();

        RefreshToken savedNewToken = refreshTokenRepository.save(newToken);
        savedNewToken.setTokenPlain(newPlainToken);
        return savedNewToken;
    }

    // ================= LOGOUT =================
    @Transactional
    public void logout(String plainToken) {
        if (!StringUtils.hasText(plainToken)) return;

        String hashedToken = hashToken(plainToken);
        refreshTokenRepository.findByTokenHash(hashedToken).ifPresent(token -> {
            token.setRevoked(true);
            token.setRevokedAt(Instant.now());
            // Entity thuá»™c Transactional sáº½ tá»± Ä‘á»™ng commit thay Ä‘á»•i xuá»‘ng DB
        });
    }

    // ================= LOGOUT ALL DEVICES =================
    @Transactional
    public void revokeAllUserTokens(Users user) {
        if (user == null || user.getId() == null) return;
        refreshTokenRepository.revokeAllByUser(user.getId(), Instant.now());
    }

    // ================= HELPER =================

    protected void revokeFamilyTokens(String familyId) {
        refreshTokenRepository.revokeAllByFamilyId(familyId, Instant.now());
    }

    /**
     * Sinh token an toÃ n báº±ng SecureRandom thay vÃ¬ UUID
     * Äá»™ dÃ i 64 bytes (512 bits) mÃ£ hÃ³a Base64 URL safe.
     */
    private String generateSecureToken() {
        byte[] randomBytes = new byte[64];
        SECURE_RANDOM.nextBytes(randomBytes);
        return BASE64_URL_ENCODER.encodeToString(randomBytes);
    }

    /**
     * BÄƒm Token báº±ng SHA-256 (Tá»‘c Ä‘á»™ cao hÆ¡n Bcrypt, an toÃ n tuyá»‡t Ä‘á»‘i vá»›i chuá»—i dÃ i sinh báº±ng SecureRandom).
     */
    private String hashToken(String plainToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Lá»—i khá»Ÿi táº¡o thuáº­t toÃ¡n mÃ£ hÃ³a SHA-256", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Lá»—i mÃ£ hÃ³a há»‡ thá»‘ng.");
        }
    }
}
