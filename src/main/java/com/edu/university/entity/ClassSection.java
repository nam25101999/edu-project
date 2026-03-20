package com.edu.university.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "class_sections")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClassSection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Giả sử có bảng Lecturer, ở đây lưu UUID tạm hoặc map ManyToOne nếu tạo entity Lecturer
    @Column(name = "lecturer_id")
    private UUID lecturerId;

    private String semester;
    private Integer year;

    // Ví dụ: "T2, 1-3" (Thứ 2, tiết 1 đến 3)
    private String schedule;

    private String room;

    @Column(nullable = false)
    private Integer maxStudents;

    // THÊM: Thời gian bắt đầu và kết thúc đăng ký tín chỉ
    private java.time.LocalDateTime registrationStartDate;
    private java.time.LocalDateTime registrationEndDate;
}