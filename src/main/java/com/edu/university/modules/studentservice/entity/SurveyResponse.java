package com.edu.university.modules.studentservice.entity;

import com.edu.university.modules.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "survey_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String answersJson;
    
    private LocalDateTime submittedAt;
}
