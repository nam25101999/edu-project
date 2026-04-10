package com.edu.university.modules.examination.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.examination.dto.request.ExamPaperRequestDTO;
import com.edu.university.modules.examination.entity.Exam;
import com.edu.university.modules.examination.entity.ExamPaper;
import com.edu.university.modules.examination.entity.ExamType;
import com.edu.university.modules.examination.repository.ExamPaperRepository;
import com.edu.university.modules.examination.repository.ExamRepository;
import com.edu.university.modules.examination.repository.ExamTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExamPaperControllerIT extends BaseIntegrationTest {

    @Autowired
    private ExamPaperRepository examPaperRepository;
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private ExamTypeRepository examTypeRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SemesterRepository semesterRepository;

    private Exam testExam;

    @BeforeEach
    void setUp() {
        ExamType examType = examTypeRepository.save(ExamType.builder()
                .name("Paper Test Type")
                .description("Paper description")
                .isActive(true)
                .build());

        Course course = courseRepository.save(Course.builder()
                .courseCode("EXAM_PAPER_101")
                .name("Exam Paper Course")
                .credits(new BigDecimal("3"))
                .isActive(true)
                .build());

        Semester semester = semesterRepository.save(Semester.builder()
                .semesterName("Hoc ky kiem thu de thi")
                .semesterCode("SEM_EXAM_PAPER")
                .build());

        testExam = examRepository.save(Exam.builder()
                .examType(examType)
                .courseClass(course)
                .semester(semester)
                .examDate(LocalDate.now().plusDays(5))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(10, 0))
                .isActive(true)
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturnCreatedPayload_WhenValidRequest() throws Exception {
        ExamPaperRequestDTO request = new ExamPaperRequestDTO();
        request.setExamId(testExam.getId());
        request.setPaperCode("DE001");
        request.setFileUrl("https://files.local/de001.pdf");

        mockMvc.perform(post("/api/exam-papers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.examId").value(testExam.getId().toString()))
                .andExpect(jsonPath("$.data.paperCode").value("DE001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByExamId_ShouldReturnPageOfPapers() throws Exception {
        examPaperRepository.save(ExamPaper.builder()
                .exam(testExam)
                .paperCode("DE_GET_01")
                .fileUrl("https://files.local/get.pdf")
                .isActive(true)
                .build());

        mockMvc.perform(get("/api/exam-papers/exam/{examId}", testExam.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].paperCode").value("DE_GET_01"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldSoftDeletePaper() throws Exception {
        ExamPaper saved = examPaperRepository.save(ExamPaper.builder()
                .exam(testExam)
                .paperCode("DE_DELETE_01")
                .fileUrl("https://files.local/delete.pdf")
                .isActive(true)
                .build());

        mockMvc.perform(delete("/api/exam-papers/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true));

        entityManager.flush();
        entityManager.clear();

        assertFalse(examPaperRepository.findById(saved.getId()).isPresent());
    }
}
