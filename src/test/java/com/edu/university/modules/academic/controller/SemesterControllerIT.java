package com.edu.university.modules.academic.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.dto.request.SemesterRequestDTO;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SemesterControllerIT extends BaseIntegrationTest {

    @Autowired
    private SemesterRepository semesterRepository;

    private Semester semester;

    @BeforeEach
    void setUp() {
        semesterRepository.deleteAll();
        semester = Semester.builder()
                .semesterCode("HK1_2023")
                .semesterName("Học kỳ 1 năm 2023-2024")
                .academicYear("2023-2024")
                .startDate(LocalDate.of(2023, 9, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .isActive(true)
                .build();
        semester = semesterRepository.save(semester);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createSemester_Success() throws Exception {
        SemesterRequestDTO requestDTO = new SemesterRequestDTO();
        requestDTO.setSemesterCode("HK2_2023");
        requestDTO.setSemesterName("Học kỳ 2 năm 2023-2024");
        requestDTO.setAcademicYear("2023-2024");
        requestDTO.setStartDate(LocalDate.of(2024, 2, 1));
        requestDTO.setEndDate(LocalDate.of(2024, 6, 30));

        mockMvc.perform(post("/api/semesters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.semesterCode").value("HK2_2023"))
                .andExpect(jsonPath("$.data.semesterName").value("Học kỳ 2 năm 2023-2024"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllSemesters_Success() throws Exception {
        mockMvc.perform(get("/api/semesters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllSemesters_Pagination_Success() throws Exception {
        // Add more data
        Semester s2 = Semester.builder()
                .semesterCode("HK2_2023")
                .semesterName("Học kỳ 2 năm 2023-2024")
                .academicYear("2023-2024")
                .startDate(LocalDate.of(2024, 2, 1))
                .endDate(LocalDate.of(2024, 6, 30))
                .isActive(true)
                .build();
        semesterRepository.save(s2);

        mockMvc.perform(get("/api/semesters")
                .param("page", "0")
                .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(2));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getSemesterById_Success() throws Exception {
        mockMvc.perform(get("/api/semesters/{id}", semester.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.semesterCode").value("HK1_2023"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateSemester_Success() throws Exception {
        SemesterRequestDTO requestDTO = new SemesterRequestDTO();
        requestDTO.setSemesterCode("HK1_2023_UPDATED");
        requestDTO.setSemesterName("Học kỳ 1 năm 2023-2024 Updated");
        requestDTO.setAcademicYear("2023-2024");

        mockMvc.perform(put("/api/semesters/{id}", semester.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.semesterCode").value("HK1_2023_UPDATED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteSemester_Success() throws Exception {
        mockMvc.perform(delete("/api/semesters/{id}", semester.getId()))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/semesters/{id}", semester.getId()))
                .andExpect(status().isNotFound());
    }
}
