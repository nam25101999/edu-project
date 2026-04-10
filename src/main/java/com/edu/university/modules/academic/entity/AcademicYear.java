package com.edu.university.modules.academic.entity;

import com.edu.university.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDateTime;

@Entity
@Table(name = "academic_years")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted_at IS NULL")
public class AcademicYear extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "academic_code", unique = true, length = 50)
    private String academicCode;

    @Column(name = "academic_name", length = 100)
    private String academicName;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @Column(length = 255)
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @OneToMany(mappedBy = "academicYear", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Semester> semesters = new ArrayList<>();

}