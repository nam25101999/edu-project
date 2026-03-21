package com.edu.university.modules.report;

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

    // === THÔNG TIN BẢO MẬT & NGƯỜI DÙNG ===
    @Column(nullable = false)
    private String username;

    private String ipAddress;

    @Column(length = 500)
    private String userAgent; // Trình duyệt/Thiết bị sử dụng

    // === THÔNG TIN NGHIỆP VỤ ===
    @Column(nullable = false)
    private String action; // CREATE, UPDATE, DELETE...

    @Column(nullable = false)
    private String entityName; // STUDENT, COURSE...

    // === THÔNG TIN DEBUG SÂU ===
    private String httpMethod; // GET, POST, PUT, DELETE

    private String endpoint; // /api/students/...

    @Column(columnDefinition = "TEXT")
    private String requestPayload; // JSON dữ liệu gửi lên

    @Column(columnDefinition = "TEXT")
    private String responsePayload; // JSON dữ liệu trả về

    // === THÔNG TIN ĐO LƯỜNG & PHÂN TÍCH ===
    private Long executionTimeMs; // Thời gian chạy API (Tính bằng mili-giây)

    @Column(nullable = false)
    private String status; // SUCCESS hoặc FAILED

    @Column(columnDefinition = "TEXT")
    private String errorMessage; // Lưu lỗi nếu API bị crash

    @Column(nullable = false)
    private LocalDateTime createdAt;
}