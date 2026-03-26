package com.edu.university.modules.student.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "majors")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Bảo vệ toàn diện
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String majorCode;

    @Column(nullable = false)
    private String name;

    // BỎ fetch = FetchType.LAZY để Hibernate nạp sẵn dữ liệu Khoa (EAGER),
    // giúp Frontend lấy được thông tin Khoa mà không bị lỗi no Session
    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    // 1 Ngành có nhiều Lớp sinh hoạt
    @OneToMany(mappedBy = "major", cascade = CascadeType.ALL)
    @JsonIgnore // Bắt buộc để tránh đệ quy vô hạn
    private List<StudentClass> studentClasses;

    // 1 Ngành có nhiều Sinh viên
    @OneToMany(mappedBy = "major", cascade = CascadeType.ALL)
    @JsonIgnore // Bắt buộc để tránh đệ quy vô hạn
    private List<Student> students;
}