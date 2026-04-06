package com.edu.university.modules.graduation.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.graduation.dto.request.GraduationConditionRequestDTO;
import com.edu.university.modules.graduation.entity.GraduationCondition;
import com.edu.university.modules.graduation.repository.GraduationConditionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

public class GraduationConditionControllerIT extends BaseIntegrationTest {

    @Autowired
    private GraduationConditionRepository graduationConditionRepository;

    @Autowired
    private TrainingProgramRepository trainingProgramRepository;

    private TrainingProgram testProgram;

    @BeforeEach
    void setUp() {
        graduationConditionRepository.deleteAll();
        
        testProgram = trainingProgramRepository.save(TrainingProgram.builder()
                .programName("IT Program")
                .programCode("IT01")
                .totalCredits(new BigDecimal("120"))
                .isActive(true)
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn201_WhenValidRequest() throws Exception {
        GraduationConditionRequestDTO request = new GraduationConditionRequestDTO();
        request.setTrainingProgramId(testProgram.getId());
        request.setAppliedCohort("2020");
        request.setMinTotalCredits(120);
        request.setMinGpa(new BigDecimal("2.00"));
        request.setEnglishRequirement("B1");

        mockMvc.perform(post("/api/graduation-conditions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appliedCohort").value("2020"))
                .andExpect(jsonPath("$.minTotalCredits").value(120))
                .andExpect(jsonPath("$.minGpa").value(2.0))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ShouldReturnListOfConditions() throws Exception {
        GraduationCondition gc1 = GraduationCondition.builder()
                .trainingProgram(testProgram)
                .appliedCohort("2020")
                .isActive(true)
                .build();
        graduationConditionRepository.save(gc1);

        mockMvc.perform(get("/api/graduation-conditions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].appliedCohort").value("2020"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ShouldReturnCondition_WhenExists() throws Exception {
        GraduationCondition gc = GraduationCondition.builder()
                .trainingProgram(testProgram)
                .appliedCohort("2021")
                .isActive(true)
                .build();
        GraduationCondition saved = graduationConditionRepository.save(gc);

        mockMvc.perform(get("/api/graduation-conditions/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.appliedCohort").value("2021"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldReturn200_WhenValidRequest() throws Exception {
        GraduationCondition gc = GraduationCondition.builder()
                .trainingProgram(testProgram)
                .appliedCohort("2020")
                .isActive(true)
                .build();
        GraduationCondition saved = graduationConditionRepository.save(gc);

        GraduationConditionRequestDTO request = new GraduationConditionRequestDTO();
        request.setTrainingProgramId(testProgram.getId());
        request.setAppliedCohort("2020-Updated");
        request.setMinTotalCredits(125);

        mockMvc.perform(put("/api/graduation-conditions/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appliedCohort").value("2020-Updated"))
                .andExpect(jsonPath("$.minTotalCredits").value(125));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn204_WhenExists() throws Exception {
        GraduationCondition gc = GraduationCondition.builder()
                .trainingProgram(testProgram)
                .appliedCohort("To be deleted")
                .isActive(true)
                .build();
        GraduationCondition saved = graduationConditionRepository.save(gc);

        mockMvc.perform(delete("/api/graduation-conditions/" + saved.getId()))
                .andExpect(status().isNoContent());

        // Verify soft delete via repository
        GraduationCondition deleted = graduationConditionRepository.findById(saved.getId()).orElse(null);
        org.junit.jupiter.api.Assertions.assertNotNull(deleted);
        org.junit.jupiter.api.Assertions.assertFalse(deleted.isActive());
        org.junit.jupiter.api.Assertions.assertNotNull(deleted.getDeletedAt());
    }
}
