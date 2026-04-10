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
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SemesterControllerIT extends BaseIntegrationTest {

    @Autowired
    private SemesterRepository semesterRepository;

    @BeforeEach
    void setUp() {
        semesterRepository.deleteAll();
    }

    @Test
    void getAll_ShouldReturn401_WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/semesters"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void create_ShouldReturn201_WhenValidRequest() throws Exception {
        SemesterRequestDTO request = new SemesterRequestDTO();
        request.setSemesterCode("20231");
        request.setSemesterName("Học kỳ 1 năm học 2023-2024");
        request.setAcademicYear("2023-2024");
        request.setStartDate(LocalDate.of(2023, 9, 4));
        request.setEndDate(LocalDate.of(2024, 1, 15));

        mockMvc.perform(post("/api/semesters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.semesterCode").value("20231"))
                .andExpect(jsonPath("$.data.semesterName").value("Học kỳ 1 năm học 2023-2024"));
    }

    @Test
    @WithMockUser
    void getAll_ShouldReturnPaginationMetadata() throws Exception {
        saveSemester("20231", "HK1");
        saveSemester("20232", "HK2");

        mockMvc.perform(get("/api/semesters")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(0));
    }

    @Test
    @WithMockUser
    void getById_ShouldReturnSemester_WhenExists() throws Exception {
        Semester semester = saveSemester("20231", "HK1");

        mockMvc.perform(get("/api/semesters/" + semester.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.semesterCode").value("20231"));
    }

    @Test
    @WithMockUser
    void update_ShouldReturn200_WhenValidRequest() throws Exception {
        Semester semester = saveSemester("20231", "Old Name");

        SemesterRequestDTO request = new SemesterRequestDTO();
        request.setSemesterCode("20231");
        request.setSemesterName("New Name");
        request.setAcademicYear("2023-2024");

        mockMvc.perform(put("/api/semesters/" + semester.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.semesterName").value("New Name"));
    }

    @Test
    @WithMockUser
    void delete_ShouldSoftDelete_WhenExists() throws Exception {
        Semester semester = saveSemester("20231", "HK1");

        mockMvc.perform(delete("/api/semesters/" + semester.getId()))
                .andExpect(status().isOk());

        Semester deleted = semesterRepository.findById(semester.getId()).orElse(null);
        org.junit.jupiter.api.Assertions.assertNotNull(deleted);
        org.junit.jupiter.api.Assertions.assertFalse(deleted.isActive());
    }

    private Semester saveSemester(String code, String name) {
        Semester semester = new Semester();
        semester.setSemesterCode(code);
        semester.setSemesterName(name);
        semester.setActive(true);
        return semesterRepository.save(semester);
    }
}
