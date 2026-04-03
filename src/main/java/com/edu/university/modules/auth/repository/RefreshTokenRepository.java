package com.edu.university.modules.auth.repository;

import com.edu.university.modules.auth.entity.RefreshToken;
import com.edu.university.modules.auth.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    // 1. Tìm token bằng mã Hash (Bảo mật: Không tìm bằng token plain text nữa)
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // 2. Thu hồi (Revoke) tất cả token của 1 User (Dùng khi Logout All Devices, Đổi mật khẩu)
    @Modifying
    @Query("UPDATE RefreshToken r SET r.isRevoked = true, r.revokedAt = :time WHERE r.user.id = :userId AND r.isRevoked = false")
    void revokeAllByUser(@Param("userId") UUID userId, @Param("time") Instant time);

    // 3. Thu hồi (Revoke) tất cả token trong 1 Family (Dùng khi phát hiện Reuse/Hack token)
    @Modifying
    @Query("UPDATE RefreshToken r SET r.isRevoked = true, r.revokedAt = :time WHERE r.familyId = :familyId AND r.isRevoked = false")
    void revokeAllByFamilyId(@Param("familyId") String familyId, @Param("time") Instant time);

    // 4. Xóa vật lý toàn bộ token của User (Có thể dùng khi xóa vĩnh viễn tài khoản)
    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(@Param("user") Users user);
}