package com.edu.university.modules.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "otp_tokens",
        indexes = {
                @Index(name = "idx_otp_lookup", columnList = "user_id, otpType, isUsed, isRevoked, deletedAt"),
                @Index(name = "idx_otp_expiry", columnList = "expiresAt, deletedAt"),
                @Index(name = "idx_otp_ip_rate_limit", columnList = "ipAddress, createdAt")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
public class OtpToken {

    public static final int MAX_ATTEMPTS = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "otp_hash", nullable = false, length = 255)
    private String otpHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    public enum OtpType {
        REGISTER, RESET_PASSWORD, VERIFY_EMAIL, TWO_FACTOR_AUTH
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "otp_type", nullable = false, length = 30)
    private OtpType otpType;

    @Builder.Default
    @Column(name = "is_used", nullable = false)
    private boolean isUsed = false;

    @Builder.Default
    @Column(name = "is_revoked", nullable = false)
    private boolean isRevoked = false;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Builder.Default
    @Column(name = "attempt_count", nullable = false)
    private int attemptCount = 0;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isLockedOut() {
        return attemptCount >= MAX_ATTEMPTS;
    }

    public boolean isValid() {
        return !isUsed && !isRevoked && deletedAt == null && !isExpired() && !isLockedOut();
    }

    public void recordFailedAttempt() {
        this.attemptCount++;
    }

    public void markAsUsed() {
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
    }

    public void revoke() {
        this.isRevoked = true;
        this.revokedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
