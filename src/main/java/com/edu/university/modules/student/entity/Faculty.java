package com.edu.university.modules.student.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "faculties")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Đặt ở mức Class để bảo vệ toàn diện mọi proxy
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String facultyCode;

    @Column(nullable = false)
    private String name;

    private String contactEmail;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 1 Khoa có nhiều Ngành
    @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL)
    @JsonIgnore // Tránh lỗi vòng lặp đệ quy vô tận khi lấy danh sách Khoa
    private List<Major> majors;
}