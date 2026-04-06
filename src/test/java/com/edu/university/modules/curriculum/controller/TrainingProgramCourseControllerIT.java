package com.edu.university.modules.curriculum.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.builders.CourseBuilder;
import com.edu.university.builders.TrainingProgramBuilder;
import com.edu.university.builders.DepartmentBuilder;
import com.edu.university.modules.curriculum.dto.request.TrainingProgramCourseRequestDTO;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.entity.TrainingProgramCourse;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.curriculum.repository.TrainingProgramCourseRepository;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
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

public class TrainingProgramCourseControllerIT extends BaseIntegrationTest {

    @Autowired
    private TrainingProgramCourseRepository tpCourseRepository;

    @Autowired
    private TrainingProgramRepository tpRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private TrainingProgram testTP;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        tpCourseRepository.deleteAll();
        tpRepository.deleteAll();
        courseRepository.deleteAll();
        departmentRepository.deleteAll();
        
        Department dept = departmentRepository.save(DepartmentBuilder.aDepartment().build());

        testTP = tpRepository.save(TrainingProgramBuilder.aTrainingProgram()
                .withProgramCode("TP_01")
                .withProgramName("Test Program")
                .withDepartment(dept)
                .build());

        testCourse = courseRepository.save(CourseBuilder.aCourse()
                .withCode("C_01")
                .withName("Test Course")
                .withDepartment(dept)
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
                .andExpect(jsonPath("$.data.trainingProgramId").value(testTP.getId().toString()))
                .andExpect(jsonPath("$.data.courseCode").value("C_01"));
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
                .andExpect(jsonPath("$.data.content", hasSize(1)));
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
                .andExpect(jsonPath("$.data.courseCode").value("C_01"));
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
                .andExpect(jsonPath("$.data.isRequired").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn200_AndSoftDelete() throws Exception {
        TrainingProgramCourse tpc = tpCourseRepository.save(TrainingProgramCourse.builder()
                .trainingProgram(testTP)
                .course(testCourse)
                .isActive(true)
                .build());

        mockMvc.perform(delete("/api/training-program-courses/" + tpc.getId()))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();
        assertFalse(tpCourseRepository.findById(tpc.getId()).isPresent());
    }
}
