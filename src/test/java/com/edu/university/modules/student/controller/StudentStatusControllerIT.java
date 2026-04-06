package com.edu.university.modules.student.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.student.dto.request.StudentStatusRequestDTO;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.entity.StudentStatus;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.student.repository.StudentStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StudentStatusControllerIT extends BaseIntegrationTest {

    @Autowired
    private StudentStatusRepository studentStatusRepository;

    @Autowired
    private StudentRepository studentRepository;

    private Student testStudent;

    @BeforeEach
    void setUp() {
        studentStatusRepository.deleteAll();
        studentRepository.deleteAll();

        // Create Student
        testStudent = Student.builder()
                .studentCode("N20DCCN001")
                .fullName("Nguyen Van A")
                .isActive(true)
                .build();
        testStudent = studentRepository.save(testStudent);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createStatus_Success() throws Exception {
        StudentStatusRequestDTO request = new StudentStatusRequestDTO();
        request.setStudentId(testStudent.getId());
        request.setStatus("Bảo lưu");
        request.setEffectiveDate(LocalDate.now());
        request.setReason("Lý do cá nhân");

        mockMvc.perform(post("/api/student-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.status").value("Bảo lưu"))
                .andExpect(jsonPath("$.data.studentCode").value("N20DCCN001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_Success() throws Exception {
        StudentStatus statusEntity = StudentStatus.builder()
                .student(testStudent)
                .statusName("Bảo lưu")
                .startDate(LocalDate.now())
                .isActive(true)
                .build();
        studentStatusRepository.save(statusEntity);

        mockMvc.perform(get("/api/student-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByStudentId_Success() throws Exception {
        StudentStatus statusEntity = StudentStatus.builder()
                .student(testStudent)
                .statusName("Bảo lưu")
                .startDate(LocalDate.now())
                .isActive(true)
                .build();
        studentStatusRepository.save(statusEntity);

        mockMvc.perform(get("/api/student-status/student/{studentId}", testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateStatus_Success() throws Exception {
        StudentStatus statusEntity = StudentStatus.builder()
                .student(testStudent)
                .statusName("Bảo lưu")
                .startDate(LocalDate.now())
                .isActive(true)
                .build();
        StudentStatus saved = studentStatusRepository.save(statusEntity);

        StudentStatusRequestDTO request = new StudentStatusRequestDTO();
        request.setStudentId(testStudent.getId());
        request.setStatus("Thôi học");
        request.setEffectiveDate(LocalDate.now());

        mockMvc.perform(put("/api/student-status/{id}", saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("Thôi học"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteStatus_Success() throws Exception {
        StudentStatus statusEntity = StudentStatus.builder()
                .student(testStudent)
                .statusName("Bảo lưu")
                .startDate(LocalDate.now())
                .isActive(true)
                .build();
        StudentStatus saved = studentStatusRepository.save(statusEntity);

        mockMvc.perform(delete("/api/student-status/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/student-status/student/{studentId}", testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(0));
    }
}
