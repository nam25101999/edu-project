package com.edu.university.modules.enrollment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "grades")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true)
    private Enrollment enrollment;

    private Double attendanceScore;
    private Double midtermScore;
    private Double finalScore;

    // Điểm tổng kết hệ 10
    private Double totalScore;

    // Điểm hệ 4
    private Double gpaScore;

    // Điểm chữ (A, B, C, D, F)
    private String letterGrade;
}