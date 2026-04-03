package com.edu.university.modules.auth.repository;

import com.edu.university.modules.auth.entity.OtpToken;
import com.edu.university.modules.auth.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, UUID> {

    /**
     * Lấy danh sách tất cả các OTP hợp lệ (chưa dùng, chưa hủy, chưa xóa) của User theo loại (Type).
     * Phục vụ việc thu hồi toàn bộ OTP cũ khi người dùng yêu cầu gửi lại OTP mới.
     */
    List<OtpToken> findByUserAndOtpTypeAndIsUsedFalseAndIsRevokedFalseAndDeletedAtIsNull(Users user, OtpToken.OtpType type);

    /**
     * Lấy ra mã OTP MỚI NHẤT (dựa vào thời gian tạo) và đang còn hiệu lực của User theo loại (Type).
     * Phục vụ việc xác thực khi người dùng nhập mã OTP.
     */
    Optional<OtpToken> findTopByUserAndOtpTypeAndIsUsedFalseAndIsRevokedFalseAndDeletedAtIsNullOrderByCreatedAtDesc(Users user, OtpToken.OtpType type);

    // Xóa toàn bộ OTP của User (Dùng khi cleanup dọn dẹp data)
    void deleteByUser(Users user);
}