package com.edu.university.modules.student.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "faculties")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String facultyCode;

    @Column(nullable = false)
    private String name;

    // Thông tin thêm (tuỳ chọn)
    private String description;
    private String contactEmail;
}