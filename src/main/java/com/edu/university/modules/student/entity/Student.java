package com.edu.university.modules.student.entity;

import com.edu.university.modules.enrollment.entity.AcademicStatus;
import com.edu.university.modules.auth.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @JsonIgnore // Bỏ qua khi parse JSON: Khắc phục lỗi "no Session" và chống lộ thông tin bảo mật (password)
    private User user;

    @Column(unique = true, nullable = false)
    private String studentCode;

    @Column(nullable = false)
    private String fullName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ngăn lỗi proxy của Hibernate khi parse JSON
    private Major major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_class_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ngăn lỗi proxy của Hibernate
    private StudentClass studentClass;


    private Integer enrollmentYear;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AcademicStatus academicStatus = AcademicStatus.BINH_THUONG;
}