package com.edu.university.modules.curriculum.controller;

import com.edu.university.BackendApplication;
import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.curriculum.dto.request.CoursePrerequisiteRequestDTO;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.entity.CoursePrerequisite;
import com.edu.university.modules.curriculum.repository.CoursePrerequisiteRepository;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class CoursePrerequisiteControllerIT extends BaseIntegrationTest {

    @Autowired
    private CoursePrerequisiteRepository cpRepository;

    @Autowired
    private CourseRepository courseRepository;

    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        cpRepository.deleteAll();
        courseRepository.deleteAll();
        
        course1 = courseRepository.save(Course.builder()
                .code("MATH101")
                .name("Mathematics I")
                .isActive(true)
                .build());

        course2 = courseRepository.save(Course.builder()
                .code("MATH102")
                .name("Mathematics II")
                .isActive(true)
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn201_WhenValid() throws Exception {
        CoursePrerequisiteRequestDTO request = new CoursePrerequisiteRequestDTO();
        request.setCourseId(course2.getId());
        request.setPrerequisiteCourseId(course1.getId());

        mockMvc.perform(post("/api/course-prerequisites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.course.code").value("MATH102"))
                .andExpect(jsonPath("$.prerequisiteCourse.code").value("MATH101"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ShouldReturnList() throws Exception {
        cpRepository.save(CoursePrerequisite.builder()
                .course(course2)
                .prerequisiteCourse(course1)
                .isActive(true)
                .build());
        
        mockMvc.perform(get("/api/course-prerequisites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ShouldReturnPrerequisite() throws Exception {
        CoursePrerequisite cp = cpRepository.save(CoursePrerequisite.builder()
                .course(course2)
                .prerequisiteCourse(course1)
                .isActive(true)
                .build());

        mockMvc.perform(get("/api/course-prerequisites/" + cp.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prerequisiteCourse.code").value("MATH101"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn204_AndSoftDelete() throws Exception {
        CoursePrerequisite cp = cpRepository.save(CoursePrerequisite.builder()
                .course(course2)
                .prerequisiteCourse(course1)
                .isActive(true)
                .build());

        mockMvc.perform(delete("/api/course-prerequisites/" + cp.getId()))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();
        assertFalse(cpRepository.findById(cp.getId()).isPresent());
    }
}
