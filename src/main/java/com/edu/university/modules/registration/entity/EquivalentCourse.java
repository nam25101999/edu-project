package com.edu.university.modules.registration.entity;

import com.edu.university.modules.curriculum.entity.Course;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "equivalent_courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
public class EquivalentCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Khóa ngoại liên kết bảng courses (Môn cũ/gốc)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_course_id")
    private Course originalCourse;

    // Khóa ngoại liên kết bảng courses (Môn mới/thay thế)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equivalent_course_id")
    private Course equivalentCourse;

    // 1: Thay thế hoàn toàn; 2: Tương đương song song
    @Column(name = "equivalence_type")
    private Integer equivalenceType;

    @Column(name = "effect_date")
    private LocalDate effectDate;

    @Column(length = 500)
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