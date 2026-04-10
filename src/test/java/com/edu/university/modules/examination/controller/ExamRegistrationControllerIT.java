package com.edu.university.modules.examination.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.examination.dto.request.ExamRegistrationRequestDTO;
import com.edu.university.modules.examination.entity.Exam;
import com.edu.university.modules.examination.entity.ExamType;
import com.edu.university.modules.examination.repository.ExamRegistrationRepository;
import com.edu.university.modules.examination.repository.ExamRepository;
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
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExamRegistrationControllerIT extends BaseIntegrationTest {

        @Autowired
        private ExamRegistrationRepository examRegistrationRepository;
        @Autowired
        private ExamRepository examRepository;
        @Autowired
        private StudentRepository studentRepository;
        @Autowired
        private ExamTypeRepository examTypeRepository;
        @Autowired
        private CourseRepository courseRepository;
        @Autowired
        private SemesterRepository semesterRepository;

        private Exam testExam;
        private Student testStudent;

        @BeforeEach
        void setUp() {
                examRegistrationRepository.deleteAll();
                examRepository.deleteAll();

                testStudent = studentRepository.save(Student.builder()
                                .studentCode("STU_EXAM")
                                .fullName("Exam Student")
                                .email("e_stu@edu.vn")
                                .build());

                ExamType type = examTypeRepository.save(ExamType.builder()
                                .name("Final")
                                .description("F2")
                                .isActive(true)
                                .build());

                Course course = courseRepository.save(Course.builder()
                                .courseCode("EXAM202")
                                .name("Comp Exam Course")
                                .credits(new BigDecimal("3"))
                                .isActive(true)
                                .build());

                Semester semester = semesterRepository.save(Semester.builder()
                                .semesterName("HK2 2023-2024")
                                .semesterCode("20232_EXAM")
                                .build());

                testExam = examRepository.save(Exam.builder()
                                .examType(type)
                                .courseClass(course)
                                .semester(semester)
                                .examDate(LocalDate.now().plusDays(10))
                                .startTime(LocalTime.of(9, 0))
                                .endTime(LocalTime.of(11, 0))
                                .isActive(true)
                                .build());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        void create_ShouldReturn201_WhenValid() throws Exception {
                ExamRegistrationRequestDTO request = new ExamRegistrationRequestDTO();
                request.setExamId(testExam.getId());
                request.setStudentId(testStudent.getId());

                mockMvc.perform(post("/api/exam-registrations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.studentId").value(testStudent.getId().toString()));
        }
}
