package com.edu.university.modules.auth.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.auth.entity.AuditLog;
import com.edu.university.modules.auth.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuditLogAspectIT extends BaseIntegrationTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "tester", roles = "ADMIN")
    void shouldLogControllerRequestWithoutExplicitAnnotation() throws Exception {
        mockMvc.perform(get("/api/academic-years"))
                .andExpect(status().isOk());

        assertThat(auditLogRepository.findAll()).hasSize(1);
        AuditLog auditLog = auditLogRepository.findAll().getFirst();
        assertThat(auditLog.getAction()).isEqualTo(AuditLog.AuditAction.READ);
        assertThat(auditLog.getEntityName()).isEqualTo("ACADEMIC_YEAR");
        assertThat(auditLog.getEndpoint()).isEqualTo("/api/academic-years");
        assertThat(auditLog.getStatus()).isEqualTo(200);
    }

    @Test
    @WithMockUser(username = "tester", roles = "ADMIN")
    void shouldLogFailedControllerRequestOnceWithBusinessStatus() throws Exception {
        UUID missingId = UUID.randomUUID();

        mockMvc.perform(get("/api/academic-years/{id}", missingId))
                .andExpect(status().isNotFound());

        assertThat(auditLogRepository.findAll()).hasSize(1);
        AuditLog auditLog = auditLogRepository.findAll().getFirst();
        assertThat(auditLog.getAction()).isEqualTo(AuditLog.AuditAction.READ);
        assertThat(auditLog.getEntityName()).isEqualTo("ACADEMIC_YEAR");
        assertThat(auditLog.getEntityId()).isEqualTo(missingId.toString());
        assertThat(auditLog.getStatus()).isEqualTo(404);
    }
}
