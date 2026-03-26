package com.edu.university.modules.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Thực thể User đã được nâng cấp với Audit, Security và Tối ưu hóa DB.
 */
@Entity
@Table(
        name = "users",
        indexes = {
                // Tối ưu hóa truy vấn bằng Index cho các trường thường xuyên được tìm kiếm (WHERE / JOIN)
                @Index(name = "idx_user_username", columnList = "username", unique = true),
                @Index(name = "idx_user_email", columnList = "email", unique = true),
                @Index(name = "idx_user_role", columnList = "role"),
                @Index(name = "idx_user_status", columnList = "is_active, is_locked")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class) // Kích hoạt tính năng tự động Audit của Spring Data JPA
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Giới hạn độ dài, ngăn không cho update username sau khi đã tạo
    @Column(unique = true, nullable = false, length = 50, updatable = false)
    private String username;

    @JsonIgnore // Bảo mật: Tuyệt đối không bao giờ trả về field này trong các JSON response API
    @Column(nullable = false, length = 255)
    private String password;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    // ==========================================
    // 1. TÍNH NĂNG BẢO MẬT (SECURITY & ACCOUNT CONTROL)
    // ==========================================

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true; // Dùng cho Xóa mềm (Soft Delete) hoặc vô hiệu hóa tài khoản

    @Builder.Default
    @Column(name = "is_locked", nullable = false)
    private boolean isLocked = false; // Khóa tài khoản tạm thời (ví dụ: do nhập sai pass nhiều lần)

    @Builder.Default
    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts = 0; // Đếm số lần đăng nhập sai để chống Brute-force attack

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt; // Lưu vết lần đăng nhập cuối cùng

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt; // Dùng để vô hiệu hóa JWT token cũ khi người dùng đổi mật khẩu

    // ==========================================
    // 2. TÍNH NĂNG KIỂM TOÁN (AUDIT LOGGING)
    // ==========================================

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", length = 50, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;
}