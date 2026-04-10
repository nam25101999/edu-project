package com.edu.university.modules.finance.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.finance.entity.Scholarship;
import com.edu.university.modules.finance.repository.ScholarshipRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ScholarshipControllerIT extends BaseIntegrationTest {

    @Autowired
    private ScholarshipRepository scholarshipRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    private Student testStudent;
    private Semester testSemester;

    @BeforeEach
    void setUp() {
        scholarshipRepository.deleteAll();
        
        testStudent = studentRepository.save(Student.builder()
                .studentCode("STU_SCHOLAR")
                .fullName("Scholarship Student")
                .email("scholarship@edu.vn")
                .build());

        testSemester = semesterRepository.save(Semester.builder()
                .semesterName("HK2 2023-2024")
                .semesterCode("20232")
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void grantScholarship_ShouldReturn200_WhenValidRequest() throws Exception {
        mockMvc.perform(post("/api/scholarships/grant")
                .param("studentId", testStudent.getId().toString())
                .param("semesterId", testSemester.getId().toString())
                .param("name", "High Achiever Scholarship")
                .param("amount", "5000000.00")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("High Achiever Scholarship"))
                .andExpect(jsonPath("$.data.amount").value(5000000.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStudentScholarships_ShouldReturnPageOfScholarships() throws Exception {
        Scholarship scholarship = Scholarship.builder()
                .student(testStudent)
                .semester(testSemester)
                .name("Excellence Award")
                .amount(new BigDecimal("3000000"))
                .status("GRANTED")
                .isActive(true)
                .build();
        scholarshipRepository.save(scholarship);

        mockMvc.perform(get("/api/scholarships/student/" + testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].name").value("Excellence Award"));
    }
}
