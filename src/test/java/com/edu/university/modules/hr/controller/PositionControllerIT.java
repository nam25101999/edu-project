package com.edu.university.modules.hr.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.builders.DepartmentBuilder;
import com.edu.university.modules.hr.dto.request.PositionRequestDTO;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.entity.Position;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import com.edu.university.modules.hr.repository.PositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PositionControllerIT extends BaseIntegrationTest {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department testDept;

    @BeforeEach
    void setUp() {
        positionRepository.deleteAll();
        departmentRepository.deleteAll();
        
        testDept = departmentRepository.save(DepartmentBuilder.aDepartment()
                .withCode("DEPT_01")
                .withName("Department 01")
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPosition_ShouldReturn201_WhenValid() throws Exception {
        PositionRequestDTO request = new PositionRequestDTO();
        request.setCode("POS_01");
        request.setName("Manager");
        request.setDepartmentId(testDept.getId());

        mockMvc.perform(post("/api/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.code").value("POS_01"))
                .andExpect(jsonPath("$.data.departmentId").value(testDept.getId().toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPosition_ShouldReturn409_WhenCodeExists() throws Exception {
        positionRepository.save(Position.builder().code("POS_01").name("Old").department(testDept).isActive(true).build());

        PositionRequestDTO request = new PositionRequestDTO();
        request.setCode("POS_01");
        request.setName("New");
        request.setDepartmentId(testDept.getId());

        mockMvc.perform(post("/api/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllPositions_ShouldReturnPaginated() throws Exception {
        positionRepository.save(Position.builder().code("P1").name("Pos 1").department(testDept).isActive(true).build());
        
        mockMvc.perform(get("/api/positions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPositionById_ShouldReturnPosition() throws Exception {
        Position p = positionRepository.save(Position.builder().code("P2").name("Pos 2").department(testDept).isActive(true).build());

        mockMvc.perform(get("/api/positions/" + p.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.code").value("P2"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPositionByCode_ShouldReturnPosition() throws Exception {
        positionRepository.save(Position.builder().code("P3").name("Pos 3").department(testDept).isActive(true).build());

        mockMvc.perform(get("/api/positions/code/P3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Pos 3"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePosition_ShouldReturnUpdated() throws Exception {
        Position p = positionRepository.save(Position.builder().code("P4").name("Pos 4").department(testDept).isActive(true).build());

        PositionRequestDTO request = new PositionRequestDTO();
        request.setCode("P4");
        request.setName("Updated Pos");
        request.setDepartmentId(testDept.getId());

        mockMvc.perform(put("/api/positions/" + p.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Updated Pos"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePosition_ShouldReturn200_AndSoftDelete() throws Exception {
        Position p = positionRepository.save(Position.builder().code("P5").name("Pos 5").department(testDept).isActive(true).build());

        mockMvc.perform(delete("/api/positions/" + p.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        entityManager.flush();
        entityManager.clear();
        assertFalse(positionRepository.findById(p.getId()).isPresent());
    }
}
