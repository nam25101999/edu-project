package com.edu.university.modules.academic.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.dto.request.CourseSectionRequestDTO;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CourseSectionControllerIT extends BaseIntegrationTest {

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    private Course testCourse;
    private Semester testSemester;

    @BeforeEach
    void setUp() {
        courseSectionRepository.deleteAll();
        courseRepository.deleteAll();
        semesterRepository.deleteAll();

        testCourse = Course.builder()
                .code("CS101")
                .name("Computer Science 101")
                .credits(new BigDecimal("3.0"))
                .courseType("REQUIRED")
                .isActive(true)
                .build();
        testCourse = courseRepository.save(testCourse);

        testSemester = Semester.builder()
                .semesterCode("HK1_2023")
                .semesterName("Học kỳ 1 năm 2023-2024")
                .academicYear("2023-2024")
                .startDate(LocalDate.of(2023, 9, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .isActive(true)
                .build();
        testSemester = semesterRepository.save(testSemester);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourseSection_Success() throws Exception {
        CourseSectionRequestDTO request = new CourseSectionRequestDTO();
        request.setClassCode("L01_CS101");
        request.setCourseId(testCourse.getId());
        request.setSemesterId(testSemester.getId());
        request.setAcademicYear("2023-2024");
        request.setMaxStudents(50);
        request.setMinStudents(10);
        request.setClassType("THEORY");

        mockMvc.perform(post("/api/course-sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.classCode").value("L01_CS101"))
                .andExpect(jsonPath("$.data.courseName").value("Computer Science 101"))
                .andExpect(jsonPath("$.data.semesterName").value("Học kỳ 1 năm 2023-2024"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCourseSections_Success() throws Exception {
        CourseSection section = CourseSection.builder()
                .classCode("L01_CS101")
                .course(testCourse)
                .semester(testSemester)
                .academicYear("2023-2024")
                .isActive(true)
                .build();
        courseSectionRepository.save(section);

        mockMvc.perform(get("/api/course-sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].classCode").value("L01_CS101"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCourseSectionById_Success() throws Exception {
        CourseSection section = CourseSection.builder()
                .classCode("L01_CS101")
                .course(testCourse)
                .semester(testSemester)
                .academicYear("2023-2024")
                .isActive(true)
                .build();
        CourseSection saved = courseSectionRepository.save(section);

        mockMvc.perform(get("/api/course-sections/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.classCode").value("L01_CS101"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCourseSection_Success() throws Exception {
        CourseSection section = CourseSection.builder()
                .classCode("L01_CS101")
                .course(testCourse)
                .semester(testSemester)
                .academicYear("2023-2024")
                .isActive(true)
                .build();
        CourseSection saved = courseSectionRepository.save(section);

        CourseSectionRequestDTO request = new CourseSectionRequestDTO();
        request.setClassCode("L01_CS101_UPDATED");
        request.setCourseId(testCourse.getId());
        request.setSemesterId(testSemester.getId());
        request.setAcademicYear("2023-2024");

        mockMvc.perform(put("/api/course-sections/{id}", saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.classCode").value("L01_CS101_UPDATED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCourseSection_Success() throws Exception {
        CourseSection section = CourseSection.builder()
                .classCode("L01_CS101")
                .course(testCourse)
                .semester(testSemester)
                .academicYear("2023-2024")
                .isActive(true)
                .build();
        CourseSection saved = courseSectionRepository.save(section);

        mockMvc.perform(delete("/api/course-sections/{id}", saved.getId()))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/course-sections/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }
}
