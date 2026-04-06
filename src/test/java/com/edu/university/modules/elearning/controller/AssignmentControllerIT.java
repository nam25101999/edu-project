package com.edu.university.modules.elearning.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.elearning.dto.request.AssignmentRequest;
import com.edu.university.modules.elearning.entity.Assignment;
import com.edu.university.modules.elearning.repository.AssignmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssignmentControllerIT extends BaseIntegrationTest {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    private Assignment assignment;
    private CourseSection courseSection;

    @BeforeEach
    void setUp() {
        assignmentRepository.deleteAll();
        
        courseSection = new CourseSection();
        courseSection.setClassCode("CS_ASSIGN_001");
        courseSection = courseSectionRepository.save(courseSection);

        assignment = new Assignment();
        assignment.setCourseSection(courseSection);
        assignment.setTitle("Bài tập mẫu");
        assignment.setDescription("Mô tả bài tập mẫu");
        assignment.setDueDate(LocalDateTime.now().plusDays(7));
        assignment.setMaxScore(10.0);
        assignment = assignmentRepository.save(assignment);
    }

    @Test
    @WithMockUser(roles = "LECTURER")
    void createAssignment_Success() throws Exception {
        AssignmentRequest request = new AssignmentRequest();
        request.setCourseSectionId(courseSection.getId());
        request.setTitle("Bài tập mới");
        request.setDescription("Mô tả bài tập mới");
        request.setDueDate(LocalDateTime.now().plusDays(5));
        request.setMaxScore(10.0);

        mockMvc.perform(post("/api/assignments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Bài tập mới"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getAssignmentsByCourseSection_Success() throws Exception {
        mockMvc.perform(get("/api/assignments/course-section/{id}", courseSection.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Bài tập mẫu"));
    }
}
