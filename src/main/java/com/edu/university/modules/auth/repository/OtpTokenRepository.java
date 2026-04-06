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
     * Láº¥y danh sÃ¡ch táº¥t cáº£ cÃ¡c OTP há»£p lá»‡ (chÆ°a dÃ¹ng, chÆ°a há»§y, chÆ°a xÃ³a) cá»§a User theo loáº¡i (Type).
     * Phá»¥c vá»¥ viá»‡c thu há»“i toÃ n bá»™ OTP cÅ© khi ngÆ°á»i dÃ¹ng yÃªu cáº§u gá»­i láº¡i OTP má»›i.
     */
    List<OtpToken> findByUserAndOtpTypeAndIsUsedFalseAndIsRevokedFalseAndDeletedAtIsNull(Users user, OtpToken.OtpType type);

    /**
     * Láº¥y ra mÃ£ OTP Má»šI NHáº¤T (dá»±a vÃ o thá»i gian táº¡o) vÃ  Ä‘ang cÃ²n hiá»‡u lá»±c cá»§a User theo loáº¡i (Type).
     * Phá»¥c vá»¥ viá»‡c xÃ¡c thá»±c khi ngÆ°á»i dÃ¹ng nháº­p mÃ£ OTP.
     */
    Optional<OtpToken> findTopByUserAndOtpTypeAndIsUsedFalseAndIsRevokedFalseAndDeletedAtIsNullOrderByCreatedAtDesc(Users user, OtpToken.OtpType type);

    // XÃ³a toÃ n bá»™ OTP cá»§a User (DÃ¹ng khi cleanup dá»n dáº¹p data)
    void deleteByUser(Users user);
}
