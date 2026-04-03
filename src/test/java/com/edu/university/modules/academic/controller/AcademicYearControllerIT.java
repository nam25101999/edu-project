package com.edu.university.modules.academic.controller;
import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.dto.request.AcademicYearRequestDTO;
import com.edu.university.modules.academic.dto.response.AcademicYearResponseDTO;
import com.edu.university.modules.academic.entity.AcademicYear;
import com.edu.university.modules.academic.repository.AcademicYearRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AcademicYearControllerIT extends BaseIntegrationTest {

    @Autowired
    private AcademicYearRepository academicYearRepository;

    private AcademicYear academicYear;

    @BeforeEach
    void setUp() {
        academicYearRepository.deleteAll();
        academicYear = new AcademicYear();
        academicYear.setAcademicCode("AY2023");
        academicYear.setAcademicName("Năm học 2023-2024");
        academicYear.setAcademicYear("2023-2024");
        academicYear.setStartDate(LocalDate.of(2023, 9, 1));
        academicYear.setEndDate(LocalDate.of(2024, 8, 31));
        academicYear.setCreatedBy("system");
        academicYear = academicYearRepository.save(academicYear);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createAcademicYear_Success() throws Exception {
        AcademicYearRequestDTO requestDTO = new AcademicYearRequestDTO();
        requestDTO.setAcademicCode("AY2024");
        requestDTO.setAcademicName("Năm học 2024-2025");
        requestDTO.setAcademicYear("2024-2025");
        requestDTO.setStartDate(LocalDate.of(2024, 9, 1));
        requestDTO.setEndDate(LocalDate.of(2025, 8, 31));

        mockMvc.perform(post("/api/academic-years")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.academicCode").value("AY2024"))
                .andExpect(jsonPath("$.academicName").value("Năm học 2024-2025"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllAcademicYears_Success() throws Exception {
        mockMvc.perform(get("/api/academic-years"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAcademicYearById_Success() throws Exception {
        mockMvc.perform(get("/api/academic-years/{id}", academicYear.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.academicCode").value("AY2023"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAcademicYear_Success() throws Exception {
        AcademicYearRequestDTO requestDTO = new AcademicYearRequestDTO();
        requestDTO.setAcademicCode("AY2023_UPDATED");
        requestDTO.setAcademicName("Năm học 2023-2024 Updated");
        requestDTO.setAcademicYear("2023-2024");

        mockMvc.perform(put("/api/academic-years/{id}", academicYear.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.academicCode").value("AY2023_UPDATED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAcademicYear_Success() throws Exception {
        mockMvc.perform(delete("/api/academic-years/{id}", academicYear.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/academic-years/{id}", academicYear.getId()))
                .andExpect(status().isNotFound());
    }
}
