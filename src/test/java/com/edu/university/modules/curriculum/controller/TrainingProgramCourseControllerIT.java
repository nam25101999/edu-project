package com.edu.university.modules.curriculum.controller;

import com.edu.university.BackendApplication;
import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.curriculum.dto.request.TrainingProgramCourseRequestDTO;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.entity.TrainingProgramCourse;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.curriculum.repository.TrainingProgramCourseRepository;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
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

public class TrainingProgramCourseControllerIT extends BaseIntegrationTest {

    @Autowired
    private TrainingProgramCourseRepository tpCourseRepository;

    @Autowired
    private TrainingProgramRepository tpRepository;

    @Autowired
    private CourseRepository courseRepository;

    private TrainingProgram testTP;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        tpCourseRepository.deleteAll();
        tpRepository.deleteAll();
        courseRepository.deleteAll();
        
        testTP = tpRepository.save(TrainingProgram.builder()
                .programCode("TP_01")
                .programName("Test Program")
                .isActive(true)
                .build());

        testCourse = courseRepository.save(Course.builder()
                .code("C_01")
                .name("Test Course")
                .credits(new BigDecimal("3"))
                .isActive(true)
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn201_WhenValid() throws Exception {
        TrainingProgramCourseRequestDTO request = new TrainingProgramCourseRequestDTO();
        request.setTrainingProgramId(testTP.getId());
        request.setCourseId(testCourse.getId());
        request.setIsRequired(true);

        mockMvc.perform(post("/api/training-program-courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trainingProgram.programCode").value("TP_01"))
                .andExpect(jsonPath("$.course.code").value("C_01"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ShouldReturnList() throws Exception {
        tpCourseRepository.save(TrainingProgramCourse.builder()
                .trainingProgram(testTP)
                .course(testCourse)
                .isActive(true)
                .build());
        
        mockMvc.perform(get("/api/training-program-courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ShouldReturnTPCourse() throws Exception {
        TrainingProgramCourse tpc = tpCourseRepository.save(TrainingProgramCourse.builder()
                .trainingProgram(testTP)
                .course(testCourse)
                .isActive(true)
                .build());

        mockMvc.perform(get("/api/training-program-courses/" + tpc.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.course.code").value("C_01"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldReturnUpdated() throws Exception {
        TrainingProgramCourse tpc = tpCourseRepository.save(TrainingProgramCourse.builder()
                .trainingProgram(testTP)
                .course(testCourse)
                .isActive(true)
                .build());

        TrainingProgramCourseRequestDTO request = new TrainingProgramCourseRequestDTO();
        request.setTrainingProgramId(testTP.getId());
        request.setCourseId(testCourse.getId());
        request.setIsRequired(false);

        mockMvc.perform(put("/api/training-program-courses/" + tpc.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isRequired").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn204_AndSoftDelete() throws Exception {
        TrainingProgramCourse tpc = tpCourseRepository.save(TrainingProgramCourse.builder()
                .trainingProgram(testTP)
                .course(testCourse)
                .isActive(true)
                .build());

        mockMvc.perform(delete("/api/training-program-courses/" + tpc.getId()))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();
        assertFalse(tpCourseRepository.findById(tpc.getId()).isPresent());
    }
}
