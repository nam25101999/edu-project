package com.edu.university.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "tuition_fees")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TuitionFee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String semester;

    @Column(nullable = false)
    private Integer year;

    private Integer totalCredits;

    private Double totalAmount;

    private Double paidAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TuitionStatus status;
}