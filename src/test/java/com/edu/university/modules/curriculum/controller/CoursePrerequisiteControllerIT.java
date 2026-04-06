package com.edu.university.modules.curriculum.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.builders.CourseBuilder;
import com.edu.university.builders.DepartmentBuilder;
import com.edu.university.modules.curriculum.dto.request.CoursePrerequisiteRequestDTO;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.entity.CoursePrerequisite;
import com.edu.university.modules.curriculum.repository.CoursePrerequisiteRepository;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CoursePrerequisiteControllerIT extends BaseIntegrationTest {

    @Autowired
    private CoursePrerequisiteRepository cpRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        cpRepository.deleteAll();
        courseRepository.deleteAll();
        departmentRepository.deleteAll();
        
        Department dept = departmentRepository.save(DepartmentBuilder.aDepartment().build());

        course1 = courseRepository.save(CourseBuilder.aCourse()
                .withCode("MATH101")
                .withName("Mathematics I")
                .withDepartment(dept)
                .build());

        course2 = courseRepository.save(CourseBuilder.aCourse()
                .withCode("MATH102")
                .withName("Mathematics II")
                .withDepartment(dept)
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
                .andExpect(jsonPath("$.data.courseName").value("Mathematics II"))
                .andExpect(jsonPath("$.data.prerequisiteCourseName").value("Mathematics I"));
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
                .andExpect(jsonPath("$.data.content", hasSize(1)));
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
                .andExpect(jsonPath("$.data.prerequisiteCourseName").value("Mathematics I"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn200_AndSoftDelete() throws Exception {
        CoursePrerequisite cp = cpRepository.save(CoursePrerequisite.builder()
                .course(course2)
                .prerequisiteCourse(course1)
                .isActive(true)
                .build());

        mockMvc.perform(delete("/api/course-prerequisites/" + cp.getId()))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();
        assertFalse(cpRepository.findById(cp.getId()).isPresent());
    }
}
