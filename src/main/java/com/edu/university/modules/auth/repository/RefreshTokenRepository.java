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

    // 1. TÃ¬m token báº±ng mÃ£ Hash (Báº£o máº­t: KhÃ´ng tÃ¬m báº±ng token plain text ná»¯a)
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // 2. Thu há»“i (Revoke) táº¥t cáº£ token cá»§a 1 User (DÃ¹ng khi Logout All Devices, Äá»•i máº­t kháº©u)
    @Modifying
    @Query("UPDATE RefreshToken r SET r.isRevoked = true, r.revokedAt = :time WHERE r.user.id = :userId AND r.isRevoked = false")
    void revokeAllByUser(@Param("userId") UUID userId, @Param("time") Instant time);

    // 3. Thu há»“i (Revoke) táº¥t cáº£ token trong 1 Family (DÃ¹ng khi phÃ¡t hiá»‡n Reuse/Hack token)
    @Modifying
    @Query("UPDATE RefreshToken r SET r.isRevoked = true, r.revokedAt = :time WHERE r.familyId = :familyId AND r.isRevoked = false")
    void revokeAllByFamilyId(@Param("familyId") String familyId, @Param("time") Instant time);

    // 4. XÃ³a váº­t lÃ½ toÃ n bá»™ token cá»§a User (CÃ³ thá»ƒ dÃ¹ng khi xÃ³a vÄ©nh viá»…n tÃ i khoáº£n)
    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(@Param("user") Users user);
}
