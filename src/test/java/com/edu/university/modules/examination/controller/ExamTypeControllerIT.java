package com.edu.university.modules.examination.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.examination.dto.request.ExamTypeRequestDTO;
import com.edu.university.modules.examination.entity.ExamType;
import com.edu.university.modules.examination.repository.ExamTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

public class ExamTypeControllerIT extends BaseIntegrationTest {

    @Autowired
    private ExamTypeRepository examTypeRepository;

    @BeforeEach
    void setUp() {
        examTypeRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn201_WhenValidRequest() throws Exception {
        ExamTypeRequestDTO request = new ExamTypeRequestDTO();
        request.setName("Thi học kỳ");
        request.setDescription("Kỳ thi kết thúc học kỳ");

        mockMvc.perform(post("/api/exam-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Thi học kỳ"))
                .andExpect(jsonPath("$.description").value("Kỳ thi kết thúc học kỳ"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ShouldReturnListOfExamTypes() throws Exception {
        ExamType et1 = ExamType.builder().name("Type 1").isActive(true).build();
        ExamType et2 = ExamType.builder().name("Type 2").isActive(true).build();
        examTypeRepository.save(et1);
        examTypeRepository.save(et2);

        mockMvc.perform(get("/api/exam-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Type 1", "Type 2")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ShouldReturnExamType_WhenExists() throws Exception {
        ExamType et = ExamType.builder().name("Mid-term").isActive(true).build();
        ExamType saved = examTypeRepository.save(et);

        mockMvc.perform(get("/api/exam-types/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.name").value("Mid-term"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldReturn200_WhenValidRequest() throws Exception {
        ExamType et = ExamType.builder().name("Old Name").isActive(true).build();
        ExamType saved = examTypeRepository.save(et);

        ExamTypeRequestDTO request = new ExamTypeRequestDTO();
        request.setName("New Name");
        request.setDescription("Updated description");

        mockMvc.perform(put("/api/exam-types/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn204_WhenExists() throws Exception {
        ExamType et = ExamType.builder().name("To be deleted").isActive(true).build();
        ExamType saved = examTypeRepository.save(et);

        mockMvc.perform(delete("/api/exam-types/" + saved.getId()))
                .andExpect(status().isNoContent());

        // Verify soft delete via repository
        ExamType deleted = examTypeRepository.findById(saved.getId()).orElse(null);
        org.junit.jupiter.api.Assertions.assertNotNull(deleted);
        org.junit.jupiter.api.Assertions.assertFalse(deleted.isActive());
        org.junit.jupiter.api.Assertions.assertNotNull(deleted.getDeletedAt());
    }
}
