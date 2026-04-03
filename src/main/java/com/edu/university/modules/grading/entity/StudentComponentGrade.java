package com.edu.university.modules.grading.entity;

import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.registration.entity.CourseRegistration;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_component_grades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
public class StudentComponentGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Liên kết với Đăng ký học phần (Nhóm VI)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id")
    private CourseRegistration courseRegistration;

    // Liên kết với Thành phần điểm
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id")
    private GradeComponent gradeComponent;

    @Column(precision = 4, scale = 2)
    private BigDecimal score;

    @Builder.Default
    @Column(name = "is_retake")
    private boolean isRetake = false;

    @Builder.Default
    @Column(name = "is_locked")
    private boolean isLocked = false;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    // Người nhập điểm (Giảng viên)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by")
    private Users gradedBy;

    @Column(length = 255)
    private String note;

    // --- Auditing & System fields ---
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public void softDelete(String deletedByActionUser) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedByActionUser;
        this.isActive = false;
    }
}