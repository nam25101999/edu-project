package com.edu.university.repository;

import com.edu.university.entity.OtpToken;
import com.edu.university.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpTokenRepository extends JpaRepository<OtpToken, UUID> {
    Optional<OtpToken> findByOtpAndUser(String otp, User user);

    // Tìm OTP gần nhất của user
    Optional<OtpToken> findTopByUserOrderByExpiryDateDesc(User user);

    void deleteByUser(User user);
}