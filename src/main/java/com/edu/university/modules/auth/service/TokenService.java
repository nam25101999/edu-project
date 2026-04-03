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
 * Token Service - Phiên bản Security Level 10 (Optimized)
 * Áp dụng: Hash Storage, Token Rotation, Reuse Detection (Family Token), SecureRandom Generation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${spring.security.jwt.refreshExpirationMs:604800000}") // 7 ngày
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    // Sử dụng SecureRandom thay vì UUID để sinh token an toàn tuyệt đối về mặt mật mã học
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

    // ================= CREATE TOKEN (MULTI-DEVICE) =================
    @Transactional
    public RefreshToken createRefreshToken(Users user, String ipAddress, String userAgent, String familyId) {

        // 1. Sinh chuỗi Token ngẫu nhiên chuẩn Cryptography
        String plainToken = generateSecureToken();

        // 2. Hash Token để lưu vào Database
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

        // Truyền Plain Token qua Transient field để trả về cho Client
        savedToken.setTokenPlain(plainToken);
        return savedToken;
    }

    // ================= ROTATE TOKEN (REUSE DETECTION) =================
    @Transactional
    public RefreshToken rotateToken(String oldPlainToken, String ipAddress, String userAgent) {

        if (!StringUtils.hasText(oldPlainToken)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token không được để trống.");
        }

        String hashedOldToken = hashToken(oldPlainToken);

        RefreshToken oldToken = refreshTokenRepository.findByTokenHash(hashedOldToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "Refresh Token không hợp lệ hoặc không tồn tại."));

        // 🔥 PHÁT HIỆN REUSE (TOKEN BỊ ĐÁNH CẮP VÀ SỬ DỤNG LẠI)
        if (oldToken.isRevoked()) {
            log.warn("🚨 CẢNH BÁO BẢO MẬT: Phát hiện sử dụng lại Token cũ! User: {} | Family: {} | IP: {}",
                    oldToken.getUser().getUsername(), oldToken.getFamilyId(), ipAddress);

            // THU HỒI TOÀN BỘ FAMILY (Cắt đứng phiên đăng nhập trên thiết bị đó)
            revokeFamilyTokens(oldToken.getFamilyId());
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Phát hiện hành vi bất thường. Phiên đăng nhập đã bị vô hiệu hóa để bảo vệ tài khoản!");
        }

        // 🔥 Kiểm tra hết hạn (Không được xóa token ở đây để giữ Audit Trail cho Reuse Detection)
        if (oldToken.isExpired()) {
            oldToken.setRevoked(true);
            // Không xóa (delete) oldToken, chỉ đánh dấu revoked để nếu hacker dùng lại token hết hạn này ta vẫn track được family
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
        }

        // 1. Sinh token mới trước
        String newPlainToken = generateSecureToken();
        String newHashedToken = hashToken(newPlainToken);

        // 2. Revoke token cũ, cập nhật thông tin
        // Lưu ý: Giả định entity RefreshToken của bạn có hàm revoke() nhận vào hash của token kế nhiệm
        oldToken.revoke(newHashedToken);
        oldToken.markAsUsed();
        // refreshTokenRepository.save(oldToken); // Không cần thiết vì oldToken là Managed Entity trong @Transactional

        // 3. Tạo token mới KẾ THỪA FAMILY ID CŨ
        RefreshToken newToken = RefreshToken.builder()
                .user(oldToken.getUser())
                .tokenHash(newHashedToken)
                .familyId(oldToken.getFamilyId()) // Giữ nguyên Family
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
            // Entity thuộc Transactional sẽ tự động commit thay đổi xuống DB
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
     * Sinh token an toàn bằng SecureRandom thay vì UUID
     * Độ dài 64 bytes (512 bits) mã hóa Base64 URL safe.
     */
    private String generateSecureToken() {
        byte[] randomBytes = new byte[64];
        SECURE_RANDOM.nextBytes(randomBytes);
        return BASE64_URL_ENCODER.encodeToString(randomBytes);
    }

    /**
     * Băm Token bằng SHA-256 (Tốc độ cao hơn Bcrypt, an toàn tuyệt đối với chuỗi dài sinh bằng SecureRandom).
     */
    private String hashToken(String plainToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Lỗi khởi tạo thuật toán mã hóa SHA-256", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Lỗi mã hóa hệ thống.");
        }
    }
}