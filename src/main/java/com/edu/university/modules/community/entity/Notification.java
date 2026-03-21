package com.edu.university.modules.community.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    // Sẽ là NULL nếu loại thông báo là TOAN_TRUONG
    // Chứa ID của ClassSection nếu loại là THEO_LOP
    // Chứa ID của User/Student nếu loại là CA_NHAN
    private UUID targetId;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}