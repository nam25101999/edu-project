package com.edu.university;

import com.edu.university.modules.report.dto.ReportDtos.DashboardOverview;
import com.edu.university.modules.report.dto.ReportDtos.FacultyStat;
import com.edu.university.modules.report.dto.ReportDtos.PassFailStat;
import com.edu.university.modules.report.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetDashboardOverview_WithAdminRole_ShouldReturn200() throws Exception {
        DashboardOverview mockDashboard = new DashboardOverview(1500L, 50L, 120L, 300L);
        when(reportService.getDashboardOverview()).thenReturn(mockDashboard);

        mockMvc.perform(get("/api/reports/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStudents").value(1500))
                .andExpect(jsonPath("$.totalLecturers").value(50))
                .andExpect(jsonPath("$.totalCourses").value(120))
                .andExpect(jsonPath("$.totalClasses").value(300));
    }

    @Test
    @WithMockUser(roles = "LECTURER")
    public void testGetDashboardOverview_WithLecturerRole_ShouldReturn403() throws Exception {
        // Module Report chỉ dành cho ADMIN, Giảng viên vào sẽ bị block
        mockMvc.perform(get("/api/reports/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetFacultyStats_ShouldReturn200() throws Exception {
        List<FacultyStat> mockStats = List.of(
                new FacultyStat("Công nghệ thông tin", 500L),
                new FacultyStat("Kinh tế", 300L)
        );
        when(reportService.getStudentsByFaculty()).thenReturn(mockStats);

        mockMvc.perform(get("/api/reports/faculty-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].faculty").value("Công nghệ thông tin"))
                .andExpect(jsonPath("$[0].count").value(500));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetPassFailRatio_ShouldReturn200() throws Exception {
        PassFailStat mockStat = new PassFailStat(850L, 150L, 85.0, 15.0);
        when(reportService.getPassFailRatio()).thenReturn(mockStat);

        mockMvc.perform(get("/api/reports/pass-fail-ratio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passCount").value(850))
                .andExpect(jsonPath("$.passRate").value(85.0));
    }
}