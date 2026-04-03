package com.edu.university.modules.curriculum.controller;

import com.edu.university.BackendApplication;
import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.curriculum.dto.request.MajorRequestDTO;
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.curriculum.repository.MajorRepository;
import com.edu.university.modules.hr.entity.Faculty;
import com.edu.university.modules.hr.repository.FacultyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class MajorControllerIT extends BaseIntegrationTest {

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    private Faculty testFaculty;

    @BeforeEach
    void setUp() {
        majorRepository.deleteAll();
        facultyRepository.deleteAll();
        
        testFaculty = facultyRepository.save(Faculty.builder()
                .code("IT_FAC")
                .name("Faculty of IT")
                .establishedYear(LocalDate.now())
                .isActive(true)
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn201_WhenValid() throws Exception {
        MajorRequestDTO request = new MajorRequestDTO();
        request.setCode("CS_MAJOR");
        request.setName("Computer Science");
        request.setFacultyId(testFaculty.getId());

        mockMvc.perform(post("/api/majors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("CS_MAJOR"))
                .andExpect(jsonPath("$.faculty.code").value("IT_FAC"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn409_WhenCodeExists() throws Exception {
        majorRepository.save(Major.builder().code("CS_MAJOR").name("Old").faculty(testFaculty).isActive(true).build());

        MajorRequestDTO request = new MajorRequestDTO();
        request.setCode("CS_MAJOR");
        request.setName("New");
        request.setFacultyId(testFaculty.getId());

        mockMvc.perform(post("/api/majors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ShouldReturnList() throws Exception {
        majorRepository.save(Major.builder().code("M1").name("Major 1").faculty(testFaculty).isActive(true).build());
        
        mockMvc.perform(get("/api/majors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ShouldReturnMajor() throws Exception {
        Major m = majorRepository.save(Major.builder().code("M2").name("Major 2").faculty(testFaculty).isActive(true).build());

        mockMvc.perform(get("/api/majors/" + m.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("M2"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldReturnUpdated() throws Exception {
        Major m = majorRepository.save(Major.builder().code("M3").name("Major 3").faculty(testFaculty).isActive(true).build());

        MajorRequestDTO request = new MajorRequestDTO();
        request.setCode("M3");
        request.setName("Updated Major");
        request.setFacultyId(testFaculty.getId());

        mockMvc.perform(put("/api/majors/" + m.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Major"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn204_AndSoftDelete() throws Exception {
        Major m = majorRepository.save(Major.builder().code("M4").name("Major 4").faculty(testFaculty).isActive(true).build());

        mockMvc.perform(delete("/api/majors/" + m.getId()))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();
        assertFalse(majorRepository.findById(m.getId()).isPresent());
    }
}
