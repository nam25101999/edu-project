package com.edu.university.modules.finance.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.finance.dto.request.StudentTuitionRequestDTO;
import com.edu.university.modules.finance.entity.StudentTuition;
import com.edu.university.modules.finance.repository.StudentTuitionRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StudentTuitionControllerIT extends BaseIntegrationTest {

    @Autowired
    private StudentTuitionRepository studentTuitionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    private Student testStudent;
    private Semester testSemester;

    @BeforeEach
    void setUp() {
        studentTuitionRepository.deleteAll();
        
        testStudent = studentRepository.save(Student.builder()
                .studentCode("STU_TUITION")
                .fullName("Tuition Student")
                .email("tuition@edu.vn")
                .build());

        testSemester = semesterRepository.save(Semester.builder()
                .semesterName("HK1 2024-2025")
                .semesterCode("20241")
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn201_WhenValidRequest() throws Exception {
        StudentTuitionRequestDTO request = new StudentTuitionRequestDTO();
        request.setStudentId(testStudent.getId());
        request.setSemesterId(testSemester.getId());
        request.setNetAmount(new BigDecimal("12000000"));
        request.setPaidAmount(BigDecimal.ZERO);
        request.setStatus(3); // 3-DEBT

        mockMvc.perform(post("/api/student-tuitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value(3));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByStudentId_ShouldReturnPageOfTuition() throws Exception {
        StudentTuition st = StudentTuition.builder()
                .student(testStudent)
                .semester(testSemester)
                .netAmount(new BigDecimal("10000000"))
                .paidAmount(BigDecimal.ZERO)
                .status(3)
                .isActive(true)
                .build();
        studentTuitionRepository.save(st);

        mockMvc.perform(get("/api/student-tuitions/student/" + testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn200_WhenExists() throws Exception {
        StudentTuition st = StudentTuition.builder()
                .student(testStudent)
                .semester(testSemester)
                .netAmount(new BigDecimal("10000000"))
                .paidAmount(BigDecimal.ZERO)
                .status(3)
                .isActive(true)
                .build();
        StudentTuition saved = studentTuitionRepository.save(st);

        mockMvc.perform(delete("/api/student-tuitions/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa học phí sinh viên thành công"));

        StudentTuition deleted = studentTuitionRepository.findById(saved.getId()).orElse(null);
        org.junit.jupiter.api.Assertions.assertNotNull(deleted);
        org.junit.jupiter.api.Assertions.assertFalse(deleted.isActive());
    }
}
