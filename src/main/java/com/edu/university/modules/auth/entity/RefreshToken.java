package com.edu.university.modules.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_rt_hash", columnList = "tokenHash", unique = true),
                @Index(name = "idx_rt_user", columnList = "user_id, isRevoked, deletedAt"),
                @Index(name = "idx_rt_family", columnList = "familyId"),
                @Index(name = "idx_rt_expiry", columnList = "expiryDate, deletedAt")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;

    @Column(name = "family_id", nullable = false, length = 100)
    private String familyId;

    @Column(name = "replaced_by_token")
    private String replacedByToken;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @Builder.Default
    @Column(name = "is_revoked", nullable = false)
    private boolean isRevoked = false;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Transient
    private String tokenPlain;

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }

    public boolean isValid() {
        return !isRevoked && deletedAt == null && !isExpired();
    }

    public void markAsUsed() {
        this.lastUsedAt = Instant.now();
    }

    public void revoke(String replacedByTokenHash) {
        this.isRevoked = true;
        this.revokedAt = Instant.now();
        this.replacedByToken = replacedByTokenHash;
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
    }
}
