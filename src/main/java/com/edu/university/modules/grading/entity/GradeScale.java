package com.edu.university.modules.grading.entity;

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
@Table(name = "grade_scales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
public class GradeScale {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "scale_code", length = 10)
    private String scaleCode;

    @Column(name = "min_score", precision = 4, scale = 2)
    private BigDecimal minScore;

    @Column(name = "max_score", precision = 4, scale = 2)
    private BigDecimal maxScore;

    @Column(name = "letter_grade", length = 2)
    private String letterGrade;

    @Column(name = "gpa_value", precision = 3, scale = 2)
    private BigDecimal gpaValue;

    @Builder.Default
    @Column(name = "is_pass")
    private boolean pass = false;

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
    private boolean active = true;

    public void softDelete(String deletedByActionUser) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedByActionUser;
        this.active = false;
    }
}