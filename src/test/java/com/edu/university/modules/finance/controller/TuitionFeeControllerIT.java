package com.edu.university.modules.finance.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.finance.dto.request.TuitionFeeRequestDTO;
import com.edu.university.modules.finance.entity.TuitionFee;
import com.edu.university.modules.finance.repository.TuitionFeeRepository;
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

public class TuitionFeeControllerIT extends BaseIntegrationTest {

    @Autowired
    private TuitionFeeRepository tuitionFeeRepository;

    @Autowired
    private TrainingProgramRepository trainingProgramRepository;

    private TrainingProgram testProgram;

    @BeforeEach
    void setUp() {
        tuitionFeeRepository.deleteAll();
        
        testProgram = trainingProgramRepository.save(TrainingProgram.builder()
                .programName("Finance Program")
                .programCode("FIN01")
                .totalCredits(new BigDecimal("130"))
                .isActive(true)
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn201_WhenValidRequest() throws Exception {
        TuitionFeeRequestDTO request = new TuitionFeeRequestDTO();
        request.setTrainingProgramId(testProgram.getId());
        request.setCourseYear("2023");
        request.setPricePerCredit(new BigDecimal("500000.00"));
        request.setBaseTuition(new BigDecimal("2000000.00"));
        request.setEffectiveDate(LocalDate.now());

        mockMvc.perform(post("/api/tuition-fees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.courseYear").value("2023"))
                .andExpect(jsonPath("$.pricePerCredit").value(500000.0))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ShouldReturnListOfTuitionFees() throws Exception {
        TuitionFee tf = TuitionFee.builder()
                .trainingProgram(testProgram)
                .courseYear("2023")
                .pricePerCredit(new BigDecimal("500000.00"))
                .isActive(true)
                .build();
        tuitionFeeRepository.save(tf);

        mockMvc.perform(get("/api/tuition-fees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].courseYear").value("2023"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ShouldReturnTuitionFee_WhenExists() throws Exception {
        TuitionFee tf = TuitionFee.builder()
                .trainingProgram(testProgram)
                .courseYear("2024")
                .pricePerCredit(new BigDecimal("600000.00"))
                .isActive(true)
                .build();
        TuitionFee saved = tuitionFeeRepository.save(tf);

        mockMvc.perform(get("/api/tuition-fees/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.courseYear").value("2024"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldReturn200_WhenValidRequest() throws Exception {
        TuitionFee tf = TuitionFee.builder()
                .trainingProgram(testProgram)
                .courseYear("2023")
                .pricePerCredit(new BigDecimal("500000.00"))
                .isActive(true)
                .build();
        TuitionFee saved = tuitionFeeRepository.save(tf);

        TuitionFeeRequestDTO request = new TuitionFeeRequestDTO();
        request.setTrainingProgramId(testProgram.getId());
        request.setCourseYear("2023-Updated");
        request.setPricePerCredit(new BigDecimal("550000.00"));

        mockMvc.perform(put("/api/tuition-fees/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseYear").value("2023-Updated"))
                .andExpect(jsonPath("$.pricePerCredit").value(550000.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn204_WhenExists() throws Exception {
        TuitionFee tf = TuitionFee.builder()
                .trainingProgram(testProgram)
                .courseYear("To be deleted")
                .pricePerCredit(new BigDecimal("100000.00"))
                .isActive(true)
                .build();
        TuitionFee saved = tuitionFeeRepository.save(tf);

        mockMvc.perform(delete("/api/tuition-fees/" + saved.getId()))
                .andExpect(status().isNoContent());

        // Verify soft delete via repository
        TuitionFee deleted = tuitionFeeRepository.findById(saved.getId()).orElse(null);
        org.junit.jupiter.api.Assertions.assertNotNull(deleted);
        org.junit.jupiter.api.Assertions.assertFalse(deleted.isActive());
        org.junit.jupiter.api.Assertions.assertNotNull(deleted.getDeletedAt());
    }
}
