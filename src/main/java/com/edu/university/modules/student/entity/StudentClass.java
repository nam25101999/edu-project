package com.edu.university.modules.student.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "student_classes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentClass {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String classCode; // VD: IT23M

    @Column(nullable = false)
    private String name; // VD: Kỹ thuật phần mềm K23

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id", nullable = false)
    private Major major; // Thuộc ngành nào

    // 1 Lớp sinh viên có nhiều Sinh viên
    @OneToMany(mappedBy = "studentClass", cascade = CascadeType.ALL)
    private List<Student> students;
}