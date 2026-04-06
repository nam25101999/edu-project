package com.edu.university.modules.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "login_sessions",
        indexes = {
                @Index(name = "idx_session_user", columnList = "user_id, isActive"),
                @Index(name = "idx_session_id", columnList = "sessionId", unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class LoginSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "session_id", nullable = false, unique = true, length = 255)
    private String sessionId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    public void revoke() {
        this.isActive = false;
        this.revokedAt = LocalDateTime.now();
    }
}
