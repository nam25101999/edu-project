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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "training_programs",
        indexes = {
                @Index(name = "idx_training_programs_program_code", columnList = "program_code"),
                @Index(name = "idx_training_programs_program_name", columnList = "program_name"),
                @Index(name = "idx_training_programs_major_id", columnList = "major_id"),
                @Index(name = "idx_training_programs_department_id", columnList = "department_id"),
                @Index(name = "idx_training_programs_is_active", columnList = "is_active")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
public class TrainingProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "program_code", length = 50)
    private String programCode;

    @Column(name = "program_name", length = 255)
    private String programName;

    @Column(name = "program_name_en", length = 255)
    private String programNameEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "degree_level", length = 50)
    private String degreeLevel;

    @Column(name = "education_type", length = 50)
    private String educationType;

    @Column(name = "total_credits", precision = 5, scale = 1)
    private BigDecimal totalCredits;

    @Column(name = "required_credits", precision = 5, scale = 1)
    private BigDecimal requiredCredits;

    @Column(name = "elective_credits", precision = 5, scale = 1)
    private BigDecimal electiveCredits;

    @Column(name = "internship_credits", precision = 5, scale = 1)
    private BigDecimal internshipCredits;

    @Column(name = "thesis_credits", precision = 5, scale = 1)
    private BigDecimal thesisCredits;

    @Column(name = "admission_year")
    private LocalDate admissionYear;

    @Column(name = "duration_years", precision = 5, scale = 1)
    private BigDecimal durationYears;

    @Column(name = "max_duration_years", precision = 5, scale = 1)
    private BigDecimal maxDurationYears;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String objectives;

    @Column(name = "learning_outcomes", columnDefinition = "NVARCHAR(MAX)")
    private String learningOutcomes;

    @Column(length = 20)
    private String version;

    @Column(length = 20)
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
