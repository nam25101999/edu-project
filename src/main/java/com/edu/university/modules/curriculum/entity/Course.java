package com.edu.university.modules.curriculum.entity;

import com.edu.university.modules.hr.entity.Department;
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
@Table(
        name = "courses",
        indexes = {
                @Index(name = "idx_courses_course_code", columnList = "course_code"),
                @Index(name = "idx_courses_name", columnList = "name"),
                @Index(name = "idx_courses_department_id", columnList = "department_id"),
                @Index(name = "idx_courses_is_active", columnList = "is_active")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "code", length = 20)
    private String courseCode;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "name_en", length = 255)
    private String courseNameEn;

    @Column(precision = 5, scale = 1)
    private BigDecimal credits;

    // Ánh xạ đúng tên cột "cource_type" như thiết kế của bạn (mặc dù có thể là typo của "course_type")
    @Column(name = "course_type", length = 20)
    private String courseType;

    @Column(name = "theory_hours", precision = 5, scale = 1)
    private BigDecimal theoryHours;

    @Column(name = "practice_hours", precision = 5, scale = 1)
    private BigDecimal practiceHours;

    @Column(name = "self_study_hours", precision = 5, scale = 1)
    private BigDecimal selfStudyHours;

    @Column(name = "internship_credits", precision = 5, scale = 1)
    private BigDecimal internshipCredits;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

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
