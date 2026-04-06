package com.edu.university.modules.studentservice.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.studentservice.dto.request.ConductScoreRequest;
import com.edu.university.modules.studentservice.entity.ConductScore;
import com.edu.university.modules.studentservice.repository.ConductScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ConductScoreControllerIT extends BaseIntegrationTest {

    @Autowired
    private ConductScoreRepository conductScoreRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    private Student student;
    private Semester semester;

    @BeforeEach
    void setUp() {
        conductScoreRepository.deleteAll();

        student = new Student();
        student.setStudentCode("S_COND_001");
        student.setFullName("Cond Student");
        student = studentRepository.save(student);

        semester = new Semester();
        semester.setSemesterCode("SEM_COND_001");
        semester.setSemesterName("Semester for Conduct");
        semester = semesterRepository.save(semester);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateConductScore_Success() throws Exception {
        ConductScoreRequest request = new ConductScoreRequest();
        request.setStudentId(student.getId());
        request.setSemesterId(semester.getId());
        request.setScore(85);

        mockMvc.perform(put("/api/conduct-scores/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.score").value(85))
                .andExpect(jsonPath("$.data.grade").value("TỐT"));
    }
}
