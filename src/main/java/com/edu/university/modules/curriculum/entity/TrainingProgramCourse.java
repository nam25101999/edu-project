package com.edu.university.modules.curriculum.entity;

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
@Table(name = "training_program_courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
public class TrainingProgramCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_program_id")
    private TrainingProgram trainingProgram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    // Lưu dư thừa theo thiết kế để tối ưu truy vấn
    @Column(name = "course_code", length = 50)
    private String courseCode;

    @Column(name = "course_name", length = 255)
    private String courseName;

    @Column(name = "semester_id", length = 50)
    private String semesterId;

    @Column(name = "semester_code", length = 100)
    private String semesterCode;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @Builder.Default
    @Column(name = "is_required")
    private boolean isRequired = true;

    @Column(name = "group_code", length = 50)
    private String groupCode;

    @Column(precision = 5, scale = 1)
    private BigDecimal credits;

    // Môn tiên quyết trong ngữ cảnh của chương trình (nếu chỉ có 1 môn).
    // Nếu có nhiều môn, chúng ta đã có bảng course_prerequisites riêng.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisite_course_id")
    private Course prerequisiteCourse;

    @Builder.Default
    @Column(name = "is_prerequisite_required")
    private boolean isPrerequisiteRequired = false;

    @Column(length = 500)
    private String note;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(length = 50)
    private String status;

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