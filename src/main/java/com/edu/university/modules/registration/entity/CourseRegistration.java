package com.edu.university.modules.registration.entity;

import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.student.entity.Student;
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
@Table(name = "course_registrations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
public class CourseRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    // Thiết kế ghi "course_class_id" nhưng bảng gốc là "course_sections"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_class_id")
    private CourseSection courseSection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_period_id")
    private RegistrationPeriod registrationPeriod;

    // 1: Học mới; 2: Học lại; 3: Cải thiện
    @Column(name = "registration_type")
    private Integer registrationType;

    // Tạm để dạng UUID vì chưa có Entity bảng Điểm (Grades)
    @Column(name = "replaced_grade_id")
    private UUID replacedGradeId;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    // 1: Thành công; 2: Chờ thanh toán; 3: Đã hủy
    @Column(name = "status")
    private Integer status;

    @Builder.Default
    @Column(name = "is_paid")
    private boolean isPaid = false;

    // Optimistic locking xử lý tranh chấp đăng ký cùng lúc
    @Version
    @Column(name = "row_version")
    private byte[] rowVersion;

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

    // Mặc định entity có cờ hoạt động kể cả khi bảng thiết kế ko mô tả tường minh
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public void softDelete(String deletedByActionUser) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedByActionUser;
        this.isActive = false;
    }
}