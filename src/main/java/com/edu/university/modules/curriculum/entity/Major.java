package com.edu.university.modules.curriculum.entity;

import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.entity.Faculty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "majors",
        indexes = {
                @Index(name = "idx_majors_major_code", columnList = "code"),
                @Index(name = "idx_majors_name", columnList = "name"),
                @Index(name = "idx_majors_department_id", columnList = "department_id"),
                @Index(name = "idx_majors_faculty_id", columnList = "faculty_id"),
                @Index(name = "idx_majors_is_active", columnList = "is_active")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "code", length = 20)
    private String majorCode;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "effective_date", length = 20)
    private String effectiveDate;

    @Column(name = "expiry_date", length = 20)
    private String expiryDate;

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
