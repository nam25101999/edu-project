package com.edu.university.modules.auth.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.auth.entity.AuditLog;
import com.edu.university.modules.auth.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuditLogControllerIT extends BaseIntegrationTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAuditLogs_All_Success() throws Exception {
        AuditLog log = AuditLog.builder()
                .userId(UUID.randomUUID())
                .action(AuditLog.AuditAction.CREATE)
                .entityName("USER")
                .status(200)
                .createdAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);

        mockMvc.perform(get("/api/admin/audit-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAuditLogs_FailedOnly_Success() throws Exception {
        AuditLog successLog = AuditLog.builder()
                .userId(UUID.randomUUID())
                .action(AuditLog.AuditAction.CREATE)
                .entityName("USER")
                .status(200)
                .createdAt(LocalDateTime.now())
                .build();
        AuditLog failedLog = AuditLog.builder()
                .userId(UUID.randomUUID())
                .action(AuditLog.AuditAction.CREATE)
                .entityName("USER")
                .status(400)
                .createdAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(successLog);
        auditLogRepository.save(failedLog);

        mockMvc.perform(get("/api/admin/audit-logs")
                .param("status", "FAILED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].status").value(400));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStatusStats_Success() throws Exception {
        AuditLog log = AuditLog.builder()
                .userId(UUID.randomUUID())
                .action(AuditLog.AuditAction.CREATE)
                .entityName("USER")
                .status(200)
                .createdAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);

        mockMvc.perform(get("/api/admin/audit-logs/analytics/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].status").value(200));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEntityStats_Success() throws Exception {
        AuditLog log = AuditLog.builder()
                .userId(UUID.randomUUID())
                .action(AuditLog.AuditAction.CREATE)
                .entityName("USER")
                .status(200)
                .createdAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);

        mockMvc.perform(get("/api/admin/audit-logs/analytics/entities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].entityName").value("USER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSlowestApis_Success() throws Exception {
        AuditLog slowLog = AuditLog.builder()
                .userId(UUID.randomUUID())
                .action(AuditLog.AuditAction.CREATE)
                .entityName("USER")
                .status(200)
                .executionTimeMs(5000L)
                .createdAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(slowLog);

        mockMvc.perform(get("/api/admin/audit-logs/analytics/slow-apis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].executionTimeMs").value(5000));
    }
}
