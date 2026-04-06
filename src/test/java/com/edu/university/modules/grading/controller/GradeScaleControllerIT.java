package com.edu.university.modules.grading.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.grading.dto.request.GradeScaleRequestDTO;
import com.edu.university.modules.grading.entity.GradeScale;
import com.edu.university.modules.grading.repository.GradeScaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;

public class GradeScaleControllerIT extends BaseIntegrationTest {

    @Autowired
    private GradeScaleRepository gradeScaleRepository;

    @BeforeEach
    void setUp() {
        gradeScaleRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn201_WhenValidRequest() throws Exception {
        GradeScaleRequestDTO request = new GradeScaleRequestDTO();
        request.setScaleCode("A");
        request.setMinScore(new BigDecimal("8.50"));
        request.setMaxScore(new BigDecimal("10.00"));
        request.setLetterGrade("A");
        request.setGpaValue(new BigDecimal("4.00"));
        request.setPass(true);

        mockMvc.perform(post("/api/grade-scales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.scaleCode").value("A"))
                .andExpect(jsonPath("$.letterGrade").value("A"))
                .andExpect(jsonPath("$.gpaValue").value(4.0))
                .andExpect(jsonPath("$.pass").value(true))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ShouldReturnListOfGradeScales() throws Exception {
        GradeScale gs1 = GradeScale.builder()
                .scaleCode("A")
                .minScore(new BigDecimal("8.5"))
                .maxScore(new BigDecimal("10.0"))
                .letterGrade("A")
                .gpaValue(new BigDecimal("4.0"))
                .pass(true)
                .build();
        GradeScale gs2 = GradeScale.builder()
                .scaleCode("B")
                .minScore(new BigDecimal("7.0"))
                .maxScore(new BigDecimal("8.4"))
                .letterGrade("B")
                .gpaValue(new BigDecimal("3.0"))
                .pass(true)
                .build();
        gradeScaleRepository.save(gs1);
        gradeScaleRepository.save(gs2);

        mockMvc.perform(get("/api/grade-scales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].scaleCode", containsInAnyOrder("A", "B")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ShouldReturnGradeScale_WhenExists() throws Exception {
        GradeScale gs = GradeScale.builder()
                .scaleCode("C")
                .minScore(new BigDecimal("5.5"))
                .maxScore(new BigDecimal("6.9"))
                .letterGrade("C")
                .gpaValue(new BigDecimal("2.0"))
                .pass(true)
                .build();
        GradeScale saved = gradeScaleRepository.save(gs);

        mockMvc.perform(get("/api/grade-scales/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.letterGrade").value("C"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldReturn200_WhenValidRequest() throws Exception {
        GradeScale gs = GradeScale.builder()
                .scaleCode("D")
                .minScore(new BigDecimal("4.0"))
                .maxScore(new BigDecimal("5.4"))
                .letterGrade("D")
                .gpaValue(new BigDecimal("1.0"))
                .pass(true)
                .build();
        GradeScale saved = gradeScaleRepository.save(gs);

        GradeScaleRequestDTO request = new GradeScaleRequestDTO();
        request.setScaleCode("D+");
        request.setMinScore(new BigDecimal("4.5"));
        request.setMaxScore(new BigDecimal("5.4"));
        request.setLetterGrade("D+");
        request.setGpaValue(new BigDecimal("1.5"));
        request.setPass(true);

        mockMvc.perform(put("/api/grade-scales/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scaleCode").value("D+"))
                .andExpect(jsonPath("$.letterGrade").value("D+"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn204_WhenExists() throws Exception {
        GradeScale gs = GradeScale.builder()
                .scaleCode("F")
                .minScore(new BigDecimal("0.0"))
                .maxScore(new BigDecimal("3.9"))
                .letterGrade("F")
                .gpaValue(new BigDecimal("0.0"))
                .pass(false)
                .build();
        GradeScale saved = gradeScaleRepository.save(gs);

        mockMvc.perform(delete("/api/grade-scales/" + saved.getId()))
                .andExpect(status().isNoContent());

        // Verify soft delete via repository
        GradeScale deleted = gradeScaleRepository.findById(saved.getId()).orElse(null);
        org.junit.jupiter.api.Assertions.assertNotNull(deleted);
        org.junit.jupiter.api.Assertions.assertFalse(deleted.isActive());
        org.junit.jupiter.api.Assertions.assertNotNull(deleted.getDeletedAt());
    }
}
