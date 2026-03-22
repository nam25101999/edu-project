package com.edu.university;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.course.dto.ExamScheduleDtos.ExamScheduleRequest;
import com.edu.university.modules.course.entity.ExamSchedule;
import com.edu.university.modules.course.entity.ExamType;
import com.edu.university.modules.course.service.ExamScheduleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ExamScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExamScheduleService examScheduleService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDetailsImpl mockStudentDetails;
    private UserDetailsImpl mockAdminDetails;

    @BeforeEach
    public void setUp() {
        // Giả lập thông tin User đang đăng nhập bằng Spring Security
        mockStudentDetails = UserDetailsImpl.builder()
                .id(UUID.randomUUID())
                .username("student_1")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_STUDENT")))
                .build();

        mockAdminDetails = UserDetailsImpl.builder()
                .id(UUID.randomUUID())
                .username("admin_1")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();
    }

    @Test
    public void testCreateExamSchedule_WithAdminRole_ShouldReturn200() throws Exception {
        ExamScheduleRequest request = new ExamScheduleRequest(
                UUID.randomUUID(),
                ExamType.CUOI_KY,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(5).plusHours(2),
                "P.502"
        );

        ExamSchedule mockExam = ExamSchedule.builder()
                .id(UUID.randomUUID())
                .examType(ExamType.CUOI_KY)
                .room("P.502")
                .build();

        when(examScheduleService.createExamSchedule(any(ExamScheduleRequest.class))).thenReturn(mockExam);

        mockMvc.perform(post("/api/exams")
                        .with(user(mockAdminDetails)) // Gắn Token của Admin
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.room").value("P.502"))
                .andExpect(jsonPath("$.examType").value("CUOI_KY"));
    }

    @Test
    public void testCreateExamSchedule_WithStudentRole_ShouldReturn403() throws Exception {
        ExamScheduleRequest request = new ExamScheduleRequest(
                UUID.randomUUID(), ExamType.GIUA_KY, LocalDateTime.now(), LocalDateTime.now(), "P.502"
        );

        mockMvc.perform(post("/api/exams")
                        .with(user(mockStudentDetails)) // Gắn Token của Sinh viên
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isForbidden()); // Cấm sinh viên xếp lịch thi
    }

    @Test
    public void testGetMyExamSchedules_ShouldReturn200() throws Exception {
        ExamSchedule mockExam = ExamSchedule.builder()
                .id(UUID.randomUUID())
                .examType(ExamType.GIUA_KY)
                .room("P.301")
                .build();

        when(examScheduleService.getMyExamSchedules(mockStudentDetails.getId())).thenReturn(List.of(mockExam));

        mockMvc.perform(get("/api/exams/my")
                        .with(user(mockStudentDetails))) // Gọi bằng tài khoản Sinh Viên
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].room").value("P.301"));
    }
}