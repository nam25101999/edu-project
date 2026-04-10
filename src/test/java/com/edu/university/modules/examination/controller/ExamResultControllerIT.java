package com.edu.university.modules.examination.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.examination.dto.request.ExamResultRequestDTO;
import com.edu.university.modules.examination.entity.Exam;
import com.edu.university.modules.examination.entity.ExamRegistration;
import com.edu.university.modules.examination.entity.ExamResult;
import com.edu.university.modules.examination.entity.ExamType;
import com.edu.university.modules.examination.repository.ExamRegistrationRepository;
import com.edu.university.modules.examination.repository.ExamRepository;
import com.edu.university.modules.examination.repository.ExamResultRepository;
import com.edu.university.modules.examination.repository.ExamTypeRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExamResultControllerIT extends BaseIntegrationTest {

    @Autowired
    private ExamResultRepository examResultRepository;
    @Autowired
    private ExamRegistrationRepository examRegistrationRepository;
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private ExamTypeRepository examTypeRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SemesterRepository semesterRepository;
    @Autowired
    private StudentRepository studentRepository;

    private ExamRegistration testRegistration;

    @BeforeEach
    void setUp() {
        Student student = studentRepository.save(Student.builder()
                .studentCode("STU_EXAM_RESULT")
                .fullName("Sinh vien diem thi")
                .email("exam.result@edu.vn")
                .isActive(true)
                .build());

        ExamType examType = examTypeRepository.save(ExamType.builder()
                .name("Result Test Type")
                .description("Result description")
                .isActive(true)
                .build());

        Course course = courseRepository.save(Course.builder()
                .courseCode("EXAM_RESULT_101")
                .name("Exam Result Course")
                .credits(new BigDecimal("3"))
                .isActive(true)
                .build());

        Semester semester = semesterRepository.save(Semester.builder()
                .semesterName("Hoc ky kiem thu ket qua thi")
                .semesterCode("SEM_EXAM_RESULT")
                .build());

        Exam exam = examRepository.save(Exam.builder()
                .examType(examType)
                .courseClass(course)
                .semester(semester)
                .examDate(LocalDate.now().plusDays(7))
                .startTime(LocalTime.of(7, 30))
                .endTime(LocalTime.of(9, 30))
                .isActive(true)
                .build());

        testRegistration = examRegistrationRepository.save(ExamRegistration.builder()
                .exam(exam)
                .student(student)
                .rollNumber("SBD001")
                .isActive(true)
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void upsert_ShouldReturnResult_WhenValidRequest() throws Exception {
        ExamResultRequestDTO request = new ExamResultRequestDTO();
        request.setRegistrationId(testRegistration.getId());
        request.setScore(new BigDecimal("8.75"));
        request.setStatus("PASSED");
        request.setGradedAt(LocalDateTime.now());
        request.setLocked(true);
        request.setAppealStatus("NONE");

        mockMvc.perform(post("/api/exam-results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.registrationId").value(testRegistration.getId().toString()))
                .andExpect(jsonPath("$.data.score").value(8.75))
                .andExpect(jsonPath("$.data.status").value("PASSED"))
                .andExpect(jsonPath("$.data.locked").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByRegistrationId_ShouldReturnStoredResult() throws Exception {
        examResultRepository.save(ExamResult.builder()
                .examRegistration(testRegistration)
                .score(new BigDecimal("9.00"))
                .status("DISTINCTION")
                .appealStatus("NONE")
                .isLocked(false)
                .isActive(true)
                .build());

        mockMvc.perform(get("/api/exam-results/registration/{registrationId}", testRegistration.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.registrationId").value(testRegistration.getId().toString()))
                .andExpect(jsonPath("$.data.status").value("DISTINCTION"))
                .andExpect(jsonPath("$.data.studentCode").value("STU_EXAM_RESULT"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldSoftDeleteResult() throws Exception {
        ExamResult saved = examResultRepository.save(ExamResult.builder()
                .examRegistration(testRegistration)
                .score(new BigDecimal("7.00"))
                .status("PASSED")
                .appealStatus("NONE")
                .isLocked(false)
                .isActive(true)
                .build());

        mockMvc.perform(delete("/api/exam-results/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true));

        entityManager.flush();
        entityManager.clear();

        assertFalse(examResultRepository.findById(saved.getId()).isPresent());
    }
}
