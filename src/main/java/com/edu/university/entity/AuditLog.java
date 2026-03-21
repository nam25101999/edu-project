package com.edu.university.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String username; // Ai đã thực hiện thao tác

    @Column(nullable = false)
    private String action; // Thao tác gì (VD: CREATE, UPDATE, DELETE, EXPORT)

    @Column(nullable = false)
    private String entityName; // Thao tác trên đối tượng nào (VD: STUDENT, COURSE)

    @Column(columnDefinition = "TEXT")
    private String details; // Chi tiết tham số gửi lên (dạng JSON String)

    private String ipAddress; // Địa chỉ IP của người dùng

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời gian thực hiện
}