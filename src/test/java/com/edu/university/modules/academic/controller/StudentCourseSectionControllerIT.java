package com.edu.university.modules.academic.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.dto.request.StudentCourseSectionRequestDTO;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.entity.StudentCourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.academic.repository.StudentCourseSectionRepository;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class StudentCourseSectionControllerIT extends BaseIntegrationTest {

    @Autowired
    private StudentCourseSectionRepository studentCourseSectionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    private Student testStudent;
    private CourseSection testCourseSection;

    @BeforeEach
    void setUp() {
        studentCourseSectionRepository.deleteAll();
        courseSectionRepository.deleteAll();
        studentRepository.deleteAll();
        courseRepository.deleteAll();
        semesterRepository.deleteAll();

        testStudent = Student.builder()
                .studentCode("STU001")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .isActive(true)
                .build();
        testStudent = studentRepository.save(testStudent);

        Course testCourse = Course.builder()
                .courseCode("CS101")
                .name("Computer Science 101")
                .credits(new BigDecimal("3.0"))
                .courseType("REQUIRED")
                .isActive(true)
                .build();
        testCourse = courseRepository.save(testCourse);

        Semester testSemester = Semester.builder()
                .semesterCode("HK1_2023")
                .semesterName("Học kỳ 1 năm 2023-2024")
                .startDate(LocalDate.of(2023, 9, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .isActive(true)
                .build();
        testSemester = semesterRepository.save(testSemester);

        testCourseSection = CourseSection.builder()
                .classCode("L01_CS101")
                .course(testCourse)
                .semester(testSemester)
                .academicYear("2023-2024")
                .isActive(true)
                .build();
        testCourseSection = courseSectionRepository.save(testCourseSection);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerStudentToClass_Success() throws Exception {
        StudentCourseSectionRequestDTO request = new StudentCourseSectionRequestDTO();
        request.setStudentId(testStudent.getId());
        request.setCourseSectionId(testCourseSection.getId());
        request.setStatus("REGISTERED");

        mockMvc.perform(post("/api/student-course-sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.studentCode").value("STU001"))
                .andExpect(jsonPath("$.data.classCode").value("L01_CS101"))
                .andExpect(jsonPath("$.data.status").value("REGISTERED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllStudentRegistrations_Success() throws Exception {
        StudentCourseSection registration = StudentCourseSection.builder()
                .student(testStudent)
                .courseSection(testCourseSection)
                .status("REGISTERED")
                .registeredAt(LocalDateTime.now())
                .isActive(true)
                .build();
        studentCourseSectionRepository.save(registration);

        mockMvc.perform(get("/api/student-course-sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStudentRegistrationById_Success() throws Exception {
        StudentCourseSection registration = StudentCourseSection.builder()
                .student(testStudent)
                .courseSection(testCourseSection)
                .status("REGISTERED")
                .registeredAt(LocalDateTime.now())
                .isActive(true)
                .build();
        StudentCourseSection saved = studentCourseSectionRepository.save(registration);

        mockMvc.perform(get("/api/student-course-sections/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.studentCode").value("STU001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteStudentRegistration_Success() throws Exception {
        StudentCourseSection registration = StudentCourseSection.builder()
                .student(testStudent)
                .courseSection(testCourseSection)
                .status("REGISTERED")
                .registeredAt(LocalDateTime.now())
                .isActive(true)
                .build();
        StudentCourseSection saved = studentCourseSectionRepository.save(registration);

        mockMvc.perform(delete("/api/student-course-sections/{id}", saved.getId()))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/student-course-sections/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }
}
