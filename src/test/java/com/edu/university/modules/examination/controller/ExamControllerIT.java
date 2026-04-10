package com.edu.university.modules.examination.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.examination.dto.request.ExamRequestDTO;
import com.edu.university.modules.examination.entity.Exam;
import com.edu.university.modules.examination.entity.ExamType;
import com.edu.university.modules.examination.repository.ExamRepository;
import com.edu.university.modules.examination.repository.ExamTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExamControllerIT extends BaseIntegrationTest {

    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private ExamTypeRepository examTypeRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SemesterRepository semesterRepository;

    private ExamType testType;
    private Course testCourse;
    private Semester testSemester;

    @BeforeEach
    void setUp() {
        examRepository.deleteAll();
        
        testType = examTypeRepository.save(ExamType.builder()
                .name("Final Exam")
                .description("FINAL")
                .isActive(true)
                .build());

        testCourse = courseRepository.save(Course.builder()
                .courseCode("EXAM101")
                .name("Exam Course")
                .credits(new BigDecimal("3"))
                .isActive(true)
                .build());

        testSemester = semesterRepository.save(Semester.builder()
                .semesterName("HK1 2023-2024")
                .semesterCode("20231_EXAM")
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn201_WhenValidRequest() throws Exception {
        ExamRequestDTO request = new ExamRequestDTO();
        request.setExamTypeId(testType.getId());
        request.setCourseClassId(testCourse.getId());
        request.setSemesterId(testSemester.getId());
        request.setExamDate(java.time.LocalDate.now().plusDays(30));
        request.setStartTime(java.time.LocalTime.of(9, 0));
        request.setDurationMinutes(120);

        mockMvc.perform(post("/api/exams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.examDate").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ShouldReturnPage() throws Exception {
        Exam exam = Exam.builder()
                .examType(testType)
                .courseClass(testCourse)
                .semester(testSemester)
                .examDate(java.time.LocalDate.now().plusDays(1))
                .startTime(java.time.LocalTime.of(8, 0))
                .endTime(java.time.LocalTime.of(10, 0))
                .isActive(true)
                .build();
        examRepository.save(exam);

        mockMvc.perform(get("/api/exams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)));
    }
}
