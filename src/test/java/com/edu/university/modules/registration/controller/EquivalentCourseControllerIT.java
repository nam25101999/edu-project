package com.edu.university.modules.registration.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.registration.dto.request.EquivalentCourseRequestDTO;
import com.edu.university.modules.registration.entity.EquivalentCourse;
import com.edu.university.modules.registration.repository.EquivalentCourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

public class EquivalentCourseControllerIT extends BaseIntegrationTest {

    @Autowired
    private EquivalentCourseRepository equivalentCourseRepository;

    @Autowired
    private CourseRepository courseRepository;

    private Course courseA;
    private Course courseB;

    @BeforeEach
    void setUp() {
        equivalentCourseRepository.deleteAll();
        
        courseA = courseRepository.save(Course.builder()
                .name("Math A")
                .courseCode("MATH01")
                .credits(new BigDecimal("3"))
                .isActive(true)
                .build());
                
        courseB = courseRepository.save(Course.builder()
                .name("Math B")
                .courseCode("MATH02")
                .credits(new BigDecimal("3"))
                .isActive(true)
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn201_WhenValidRequest() throws Exception {
        EquivalentCourseRequestDTO request = new EquivalentCourseRequestDTO();
        request.setOriginalCourseId(courseA.getId());
        request.setEquivalentCourseId(courseB.getId());
        request.setEquivalenceType(1);
        request.setEffectDate(LocalDate.now());

        mockMvc.perform(post("/api/equivalent-courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalCourseId").value(courseA.getId().toString()))
                .andExpect(jsonPath("$.equivalentCourseId").value(courseB.getId().toString()))
                .andExpect(jsonPath("$.equivalenceType").value(1))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ShouldReturnListOfEquivalentCourses() throws Exception {
        EquivalentCourse ec = EquivalentCourse.builder()
                .originalCourse(courseA)
                .equivalentCourse(courseB)
                .equivalenceType(2)
                .isActive(true)
                .build();
        equivalentCourseRepository.save(ec);

        mockMvc.perform(get("/api/equivalent-courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].equivalenceType").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ShouldReturnEquivalentCourse_WhenExists() throws Exception {
        EquivalentCourse ec = EquivalentCourse.builder()
                .originalCourse(courseA)
                .equivalentCourse(courseB)
                .equivalenceType(1)
                .isActive(true)
                .build();
        EquivalentCourse saved = equivalentCourseRepository.save(ec);

        mockMvc.perform(get("/api/equivalent-courses/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.originalCourseCode").value("MATH01"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn204_WhenExists() throws Exception {
        EquivalentCourse ec = EquivalentCourse.builder()
                .originalCourse(courseA)
                .equivalentCourse(courseB)
                .equivalenceType(1)
                .isActive(true)
                .build();
        EquivalentCourse saved = equivalentCourseRepository.save(ec);

        mockMvc.perform(delete("/api/equivalent-courses/" + saved.getId()))
                .andExpect(status().isNoContent());

        // Verify soft delete via repository
        EquivalentCourse deleted = equivalentCourseRepository.findById(saved.getId()).orElse(null);
        org.junit.jupiter.api.Assertions.assertNotNull(deleted);
        org.junit.jupiter.api.Assertions.assertFalse(deleted.isActive());
        org.junit.jupiter.api.Assertions.assertNotNull(deleted.getDeletedAt());
    }
}
