package com.edu.university.modules.student.entity;

import com.edu.university.modules.academic.entity.AcademicYear;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.hr.entity.Department;
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
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "student_code", unique = true, length = 20)
    private String studentCode;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // 1: Nam, 2: Nữ, 0: Khác
    @Column(length = 10)
    private String gender;

    @Column(name = "personal_identification_number", length = 20)
    private String personalIdentificationNumber;

    @Column(name = "date_of_issue")
    private LocalDate dateOfIssue;

    @Column(name = "card_place", length = 100)
    private String cardPlace;

    @Column(length = 300)
    private String address;

    @Column(name = "current_address", length = 300)
    private String currentAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_years_id")
    private AcademicYear academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private TrainingProgram trainingProgram;

    @Column(length = 50)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_classes_id", nullable = false)
    private StudentClass studentClass;

    @Column(name = "admission_year")
    private LocalDateTime admissionYear;

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
