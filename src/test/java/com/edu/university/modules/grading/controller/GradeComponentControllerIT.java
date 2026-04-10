package com.edu.university.modules.grading.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.grading.dto.request.GradeComponentRequestDTO;
import com.edu.university.modules.grading.entity.GradeComponent;
import com.edu.university.modules.grading.repository.GradeComponentRepository;
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

public class GradeComponentControllerIT extends BaseIntegrationTest {

    @Autowired
    private GradeComponentRepository gradeComponentRepository;
    @Autowired
    private CourseSectionRepository courseSectionRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SemesterRepository semesterRepository;

    private CourseSection testSection;

    @BeforeEach
    void setUp() {
        gradeComponentRepository.deleteAll();

        Course course = courseRepository.save(Course.builder()
                .courseCode("GRD101")
                .name("Grading Course")
                .credits(new BigDecimal("3"))
                .isActive(true)
                .build());

        Semester semester = semesterRepository.save(Semester.builder()
                .semesterName("HK1 2023-2024")
                .semesterCode("20231_GRD")
                .build());

        testSection = courseSectionRepository.save(CourseSection.builder()
                .sectionCode("GRD101_01")
                .course(course)
                .semester(semester)
                .capacity(30)
                .isActive(true)
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn201_WhenValidRequest() throws Exception {
        GradeComponentRequestDTO request = new GradeComponentRequestDTO();
        request.setComponentCode("MID_01");
        request.setComponentName("Midterm");
        request.setWeightPercentage(new BigDecimal("0.3"));
        request.setCourseSectionId(testSection.getId());

        mockMvc.perform(post("/api/grade-components")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.componentName").value("Midterm"))
                .andExpect(jsonPath("$.data.weightPercentage").value(0.3));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByCourseSectionId_ShouldReturnPage() throws Exception {
        GradeComponent component = GradeComponent.builder()
                .componentName("Final")
                .weightPercentage(new BigDecimal("0.7"))
                .courseSection(testSection)
                .isActive(true)
                .build();
        gradeComponentRepository.save(component);

        mockMvc.perform(get("/api/grade-components/course-section/" + testSection.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn200_WhenExists() throws Exception {
        GradeComponent component = GradeComponent.builder()
                .componentName("To Delete")
                .weightPercentage(new BigDecimal("0.1"))
                .courseSection(testSection)
                .isActive(true)
                .build();
        GradeComponent saved = gradeComponentRepository.save(component);

        mockMvc.perform(delete("/api/grade-components/" + saved.getId()))
                .andExpect(status().isOk());

        GradeComponent deleted = gradeComponentRepository.findById(saved.getId()).orElse(null);
        org.junit.jupiter.api.Assertions.assertNotNull(deleted);
        org.junit.jupiter.api.Assertions.assertFalse(deleted.isActive());
    }
}
