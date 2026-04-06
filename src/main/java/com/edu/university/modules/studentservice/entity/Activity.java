package com.edu.university.modules.studentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer maxParticipants;
    
    @Builder.Default
    private Integer currentParticipants = 0;
    
    private Integer points; // Điểm rèn luyện đạt được khi tham gia
}
