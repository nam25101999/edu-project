package com.edu.university;

import com.edu.university.modules.enrollment.dto.GradeRequest;
import com.edu.university.modules.enrollment.entity.Grade;
import com.edu.university.modules.enrollment.repository.GradeRepository;
import com.edu.university.modules.enrollment.service.GradeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GradeService gradeService;

    @MockBean
    private GradeRepository gradeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "LECTURER")
    public void testEnterGrade_WithLecturerRole_ShouldReturn200() throws Exception {
        UUID enrollmentId = UUID.randomUUID();
        GradeRequest request = new GradeRequest(10.0, 8.0, 9.0);

        Grade mockGrade = Grade.builder()
                .id(UUID.randomUUID())
                .attendanceScore(10.0)
                .midtermScore(8.0)
                .finalScore(9.0)
                .totalScore(8.8)
                .letterGrade("A")
                .gpaScore(4.0)
                .build();

        when(gradeService.enterGrade(eq(enrollmentId), any(GradeRequest.class))).thenReturn(mockGrade);

        mockMvc.perform(post("/api/grades/enrollment/{enrollmentId}", enrollmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalScore").value(8.8))
                .andExpect(jsonPath("$.letterGrade").value("A"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void testEnterGrade_WithStudentRole_ShouldReturn403() throws Exception {
        UUID enrollmentId = UUID.randomUUID();
        GradeRequest request = new GradeRequest(10.0, 8.0, 9.0);

        // Sinh viên không được phép nhập điểm
        mockMvc.perform(post("/api/grades/enrollment/{enrollmentId}", enrollmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void testGetStudentGPA_ShouldReturn200() throws Exception {
        UUID studentId = UUID.randomUUID();
        when(gradeService.calculateCumulativeGPA(studentId)).thenReturn(3.6);

        mockMvc.perform(get("/api/grades/student/{studentId}/gpa", studentId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(studentId.toString()))
                .andExpect(jsonPath("$.cumulativeGPA").value(3.6));
    }
}