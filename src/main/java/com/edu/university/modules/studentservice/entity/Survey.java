package com.edu.university.modules.studentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "surveys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;
    
    @Builder.Default
    private boolean isActive = true;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
