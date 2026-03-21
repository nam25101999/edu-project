package com.edu.university.modules.student.entity;

import com.edu.university.modules.enrollment.entity.AcademicStatus;
import com.edu.university.modules.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "students")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(unique = true, nullable = false)
    private String studentCode;

    @Column(nullable = false)
    private String fullName;

    // XÓA TRƯỜNG STRING CŨ
    // private String faculty;

    // THÊM LIÊN KẾT ĐẾN NGÀNH HỌC (Từ Major sẽ truy xuất được Faculty)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_class_id")
    private StudentClass studentClass;


    private Integer enrollmentYear;

    private String avatarUrl;

    // THÊM: Trạng thái học vụ của sinh viên (Mặc định là bình thường)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AcademicStatus academicStatus = AcademicStatus.BINH_THUONG;
}