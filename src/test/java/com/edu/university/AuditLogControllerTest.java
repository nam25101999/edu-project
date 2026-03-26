package com.edu.university;

import com.edu.university.modules.report.AuditLog;
import com.edu.university.modules.report.reponsitory.AuditLogRepository;
import com.edu.university.modules.report.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditLogService auditLogService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAuditLogs_WithAdminRole_ShouldReturn200() throws Exception {
        AuditLog mockLog = AuditLog.builder()
                .id(UUID.randomUUID())
                .username("admin_test")
                .action("CREATE_COURSE")
                .status("SUCCESS")
                .createdAt(LocalDateTime.now())
                .build();

        Page<AuditLog> logPage = new PageImpl<>(List.of(mockLog));

        when(auditLogService.getAllLogs(any(PageRequest.class))).thenReturn(logPage);

        mockMvc.perform(get("/api/audit-logs")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].username").value("admin_test"))
                .andExpect(jsonPath("$.data.content[0].action").value("CREATE_COURSE"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void testGetAuditLogs_WithStudentRole_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/audit-logs"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetStatusStats_ShouldReturn200() throws Exception {

        // ✅ Mock projection đúng kiểu
        AuditLogRepository.StatusCount status1 = mock(AuditLogRepository.StatusCount.class);
        when(status1.getStatus()).thenReturn("SUCCESS");
        when(status1.getCount()).thenReturn(150L);

        AuditLogRepository.StatusCount status2 = mock(AuditLogRepository.StatusCount.class);
        when(status2.getStatus()).thenReturn("FAILED");
        when(status2.getCount()).thenReturn(5L);

        when(auditLogService.getStatusStatistics())
                .thenReturn(List.of(status1, status2));

        mockMvc.perform(get("/api/audit-logs/analytics/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].status").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].count").value(150));
    }
}