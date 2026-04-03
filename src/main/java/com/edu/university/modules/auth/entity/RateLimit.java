package com.edu.university.modules.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "rate_limits",
        indexes = {
                @Index(name = "idx_rate_limit_lookup", columnList = "targetKey, actionType")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "target_key", nullable = false, length = 255)
    private String targetKey;

    public enum RateLimitAction {
        LOGIN_ATTEMPT, OTP_REQUEST, PASSWORD_RESET_REQUEST, API_GLOBAL
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private RateLimitAction actionType;

    @Builder.Default
    @Column(name = "request_count", nullable = false)
    private int requestCount = 1;

    @Column(name = "window_start", nullable = false)
    private LocalDateTime windowStart;

    @Column(name = "blocked_until")
    private LocalDateTime blockedUntil;

    public void increment() {
        this.requestCount++;
    }

    public boolean isBlocked() {
        return blockedUntil != null && LocalDateTime.now().isBefore(blockedUntil);
    }
}