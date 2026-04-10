package com.edu.university.modules.auth.mapper;

import com.edu.university.modules.auth.dto.UserResponseDTO;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.Users;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AuthMapper {

    public UserResponseDTO toUserResponseDTO(Users user) {
        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .isActive(user.isActive())
                .emailVerified(user.isEmailVerified())
                .lastLoginAt(user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null)
                .build();
    }

    public com.edu.university.modules.auth.dto.AuditLogResponseDTO toAuditLogResponseDTO(com.edu.university.modules.auth.entity.AuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }

        return com.edu.university.modules.auth.dto.AuditLogResponseDTO.builder()
                .id(auditLog.getId())
                .userId(auditLog.getUserId())
                .action(auditLog.getAction())
                .entityName(auditLog.getEntityName())
                .entityId(auditLog.getEntityId())
                .endpoint(auditLog.getEndpoint())
                .httpMethod(auditLog.getHttpMethod())
                .status(auditLog.getStatus())
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .requestPayload(auditLog.getRequestPayload())
                .responsePayload(auditLog.getResponsePayload())
                .errorMessage(auditLog.getErrorMessage())
                .executionTimeMs(auditLog.getExecutionTimeMs())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}
