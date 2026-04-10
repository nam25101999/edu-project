package com.edu.university.modules.graduation.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.graduation.dto.request.GraduationResultRequestDTO;
import com.edu.university.modules.graduation.entity.GraduationCondition;
import com.edu.university.modules.graduation.entity.GraduationResult;
import com.edu.university.modules.graduation.repository.GraduationConditionRepository;
import com.edu.university.modules.graduation.repository.GraduationResultRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GraduationResultControllerIT extends BaseIntegrationTest {

    @Autowired
    private GraduationResultRepository graduationResultRepository;
    @Autowired
    private GraduationConditionRepository graduationConditionRepository;
    @Autowired
    private TrainingProgramRepository trainingProgramRepository;
    @Autowired
    private StudentRepository studentRepository;

    private Student testStudent;
    private GraduationCondition testCondition;

    @BeforeEach
    void setUp() {
        testStudent = studentRepository.save(Student.builder()
                .studentCode("STU_GRAD_RESULT")
                .fullName("Sinh vien tot nghiep")
                .email("graduation.result@edu.vn")
                .isActive(true)
                .build());

        TrainingProgram program = trainingProgramRepository.save(TrainingProgram.builder()
                .programCode("GRAD_PROG_01")
                .programName("Chuong trinh tot nghiep")
                .totalCredits(new BigDecimal("120"))
                .isActive(true)
                .build());

        testCondition = graduationConditionRepository.save(GraduationCondition.builder()
                .trainingProgram(program)
                .appliedCohort("2022")
                .minTotalCredits(120)
                .minGpa(new BigDecimal("2.50"))
                .maxFailedCredits(0)
                .englishRequirement("B1")
                .isActive(true)
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturnCreatedPayload_WhenValidRequest() throws Exception {
        GraduationResultRequestDTO request = new GraduationResultRequestDTO();
        request.setStudentId(testStudent.getId());
        request.setConditionId(testCondition.getId());
        request.setGpa(new BigDecimal("3.45"));
        request.setTotalCredits(128);
        request.setFailedCredits(0);
        request.setResult(1);
        request.setClassification(2);
        request.setDecisionDate(LocalDate.now());
        request.setNote("Du dieu kien tot nghiep");

        mockMvc.perform(post("/api/graduation-results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.studentId").value(testStudent.getId().toString()))
                .andExpect(jsonPath("$.data.conditionId").value(testCondition.getId().toString()))
                .andExpect(jsonPath("$.data.totalCredits").value(128));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByStudentId_ShouldReturnPageOfGraduationResults() throws Exception {
        graduationResultRepository.save(GraduationResult.builder()
                .student(testStudent)
                .graduationCondition(testCondition)
                .gpa(new BigDecimal("3.20"))
                .totalCredits(122)
                .failedCredits(0)
                .result(1)
                .classification(3)
                .decisionDate(LocalDate.now())
                .note("Dat")
                .isActive(true)
                .build());

        mockMvc.perform(get("/api/graduation-results/student/{studentId}", testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].studentCode").value("STU_GRAD_RESULT"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldSoftDeleteGraduationResult() throws Exception {
        GraduationResult saved = graduationResultRepository.save(GraduationResult.builder()
                .student(testStudent)
                .graduationCondition(testCondition)
                .gpa(new BigDecimal("2.90"))
                .totalCredits(120)
                .failedCredits(0)
                .result(1)
                .classification(4)
                .decisionDate(LocalDate.now())
                .note("Cho cong bo")
                .isActive(true)
                .build());

        mockMvc.perform(delete("/api/graduation-results/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true));

        entityManager.flush();
        entityManager.clear();

        assertFalse(graduationResultRepository.findById(saved.getId()).isPresent());
    }
}
