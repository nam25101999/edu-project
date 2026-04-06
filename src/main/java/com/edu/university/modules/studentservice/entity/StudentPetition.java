package com.edu.university.modules.studentservice.entity;

import com.edu.university.modules.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_petitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentPetition {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;
    
    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED
    
    private String attachmentUrl;
    
    private LocalDateTime createdAt;
    
    private String responseContent; // Phản hồi từ ban quản lý
}
