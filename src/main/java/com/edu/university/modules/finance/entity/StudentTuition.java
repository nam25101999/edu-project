package com.edu.university.modules.finance.entity;

import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.student.entity.Student;
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
@Table(name = "student_tuition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
public class StudentTuition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Liên kết với bảng Sinh viên (Nhóm II)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    // Liên kết với bảng Học kỳ (Nhóm V)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    // Liên kết với bảng Định mức học phí
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tuition_fee_id")
    private TuitionFee tuitionFee;

    @Column(name = "total_credits")
    private Integer totalCredits;

    @Column(name = "raw_amount", precision = 15, scale = 2)
    private BigDecimal rawAmount;

    @Column(name = "scholarship_deduction", precision = 15, scale = 2)
    private BigDecimal scholarshipDeduction;

    @Column(name = "exemption_amount", precision = 15, scale = 2)
    private BigDecimal exemptionAmount;

    @Column(name = "net_amount", precision = 15, scale = 2)
    private BigDecimal netAmount;

    @Column(name = "paid_amount", precision = 15, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "debt_amount", precision = 15, scale = 2)
    private BigDecimal debtAmount;

    // 1-PAID, 2-PARTIAL, 3-DEBT, 4-OVERDUE
    @Column(name = "status")
    private Integer status;

    @Column(name = "deadline")
    private LocalDate deadline;

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