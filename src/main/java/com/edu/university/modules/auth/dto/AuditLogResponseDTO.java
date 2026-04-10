package com.edu.university.modules.auth.dto;

import com.edu.university.modules.auth.entity.AuditLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponseDTO {
    private UUID id;
    private UUID userId;
    private String username;
    private AuditLog.AuditAction action;
    private String entityName;
    private String entityId;
    private String endpoint;
    private String httpMethod;
    private Integer status;
    private String ipAddress;
    private String userAgent;
    private String requestPayload;
    private String responsePayload;
    private String errorMessage;
    private Long executionTimeMs;
    private LocalDateTime createdAt;
}
