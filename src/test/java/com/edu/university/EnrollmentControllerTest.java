package com.edu.university;

import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.enrollment.dto.EnrollmentRequest;
import com.edu.university.modules.enrollment.entity.Enrollment;
import com.edu.university.modules.enrollment.service.EnrollmentService;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnrollmentService enrollmentService;

    @MockBean
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDetailsImpl mockStudentDetails;
    private UserDetailsImpl mockAdminDetails;
    private Student mockStudent;

    @BeforeEach
    public void setUp() {
        UUID userId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        // Giả lập thông tin User (Student) đăng nhập
        mockStudentDetails = UserDetailsImpl.builder()
                .id(userId)
                .username("student_test")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_STUDENT")))
                .build();

        // Giả lập thông tin User (Admin) đăng nhập
        mockAdminDetails = UserDetailsImpl.builder()
                .id(UUID.randomUUID())
                .username("admin_test")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();

        // Giả lập Student DB
        mockStudent = Student.builder()
                .id(studentId)
                .studentCode("SV001")
                .fullName("Nguyen Van A")
                .build();
    }

    @Test
    public void testEnrollCourse_WithStudentRole_ShouldReturn200() throws Exception {
        UUID classSectionId = UUID.randomUUID();
        EnrollmentRequest request = new EnrollmentRequest(classSectionId);

        Enrollment mockEnrollment = Enrollment.builder()
                .id(UUID.randomUUID())
                .build();

        when(studentRepository.findByUserId(mockStudentDetails.getId())).thenReturn(Optional.of(mockStudent));
        when(enrollmentService.enroll(eq(mockStudent.getId()), eq(classSectionId))).thenReturn(mockEnrollment);

        mockMvc.perform(post("/api/enrollments")
                        .with(user(mockStudentDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterClassForStudent_WithAdminRole_ShouldReturn200() throws Exception {
        UUID studentId = mockStudent.getId();
        UUID classSectionId = UUID.randomUUID();
        EnrollmentRequest request = new EnrollmentRequest(classSectionId);

        Enrollment mockEnrollment = Enrollment.builder()
                .id(UUID.randomUUID())
                .build();

        when(enrollmentService.enroll(eq(studentId), eq(classSectionId))).thenReturn(mockEnrollment);

        mockMvc.perform(post("/api/students/{studentId}/register-class", studentId)
                        .with(user(mockAdminDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}