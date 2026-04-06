package com.edu.university.modules.studentservice.entity;

import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "conduct_scores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConductScore {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id")
    private Semester semester;
    
    private Integer score;
    
    private String grade; // XUẤT SẮC, TỐT, KHÁ, TRUNG BÌNH, YẾU, KÉM
}
