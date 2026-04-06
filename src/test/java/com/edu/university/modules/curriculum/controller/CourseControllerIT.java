package com.edu.university.modules.curriculum.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.curriculum.dto.request.CourseRequestDTO;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CourseControllerIT extends BaseIntegrationTest {

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCourseSuccess() throws Exception {
        CourseRequestDTO request = new CourseRequestDTO();
        request.setCode("CS101");
        request.setName("Introduction to Computer Science");
        request.setCredits(new BigDecimal("3.0"));
        request.setCourseType("REQUIRED");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.code").value("CS101"))
                .andExpect(jsonPath("$.data.name").value("Introduction to Computer Science"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllCourses() throws Exception {
        Course course1 = Course.builder()
                .code("CS101")
                .name("Course 1")
                .credits(new BigDecimal("3.0"))
                .isActive(true)
                .build();
        Course course2 = Course.builder()
                .code("CS102")
                .name("Course 2")
                .credits(new BigDecimal("4.0"))
                .isActive(true)
                .build();
        courseRepository.save(course1);
        courseRepository.save(course2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.content[0].code").value("CS101"))
                .andExpect(jsonPath("$.data.content[1].code").value("CS102"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetCourseById() throws Exception {
        Course course = Course.builder()
                .code("CS101")
                .name("Course 1")
                .credits(new BigDecimal("3.0"))
                .isActive(true)
                .build();
        course = courseRepository.save(course);
        UUID id = course.getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/courses/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("CS101"))
                .andExpect(jsonPath("$.data.name").value("Course 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateCourse() throws Exception {
        Course course = Course.builder()
                .code("CS101")
                .name("Old Name")
                .credits(new BigDecimal("3.0"))
                .isActive(true)
                .build();
        course = courseRepository.save(course);
        UUID id = course.getId();

        CourseRequestDTO updateRequest = new CourseRequestDTO();
        updateRequest.setCode("CS101");
        updateRequest.setName("New Name");
        updateRequest.setCredits(new BigDecimal("3.5"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/courses/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("New Name"))
                .andExpect(jsonPath("$.data.credits").value(3.5));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteCourse() throws Exception {
        Course course = Course.builder()
                .code("CS101")
                .name("Course to Delete")
                .credits(new BigDecimal("3.0"))
                .isActive(true)
                .build();
        course = courseRepository.save(course);
        UUID id = course.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/courses/" + id))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/courses/" + id))
                .andExpect(status().isNotFound());
    }
}