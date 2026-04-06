package com.edu.university.modules.hr.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.hr.dto.request.DepartmentRequestDTO;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DepartmentControllerIT extends BaseIntegrationTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        departmentRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDepartment_ShouldReturn201_WhenValid() throws Exception {
        DepartmentRequestDTO request = new DepartmentRequestDTO();
        request.setCode("IT_DEPT");
        request.setName("Information Technology");

        mockMvc.perform(post("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.code").value("IT_DEPT"))
                .andExpect(jsonPath("$.data.name").value("Information Technology"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDepartment_ShouldReturn409_WhenCodeExists() throws Exception {
        Department d = Department.builder().code("IT_DEPT").name("Old").isActive(true).build();
        departmentRepository.save(d);

        DepartmentRequestDTO request = new DepartmentRequestDTO();
        request.setCode("IT_DEPT");
        request.setName("New");

        mockMvc.perform(post("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllDepartments_ShouldReturnPaginated() throws Exception {
        departmentRepository.save(Department.builder().code("D1").name("Name 1").isActive(true).build());
        
        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDepartmentById_ShouldReturnDepartment() throws Exception {
        Department d = departmentRepository.save(Department.builder().code("D2").name("Name 2").isActive(true).build());

        mockMvc.perform(get("/api/departments/" + d.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.code").value("D2"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDepartmentByCode_ShouldReturnDepartment() throws Exception {
        departmentRepository.save(Department.builder().code("D3").name("Name 3").isActive(true).build());

        mockMvc.perform(get("/api/departments/code/D3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Name 3"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateDepartment_ShouldReturnUpdated() throws Exception {
        Department d = departmentRepository.save(Department.builder().code("D4").name("Name 4").isActive(true).build());

        DepartmentRequestDTO request = new DepartmentRequestDTO();
        request.setCode("D4");
        request.setName("Updated Name");

        mockMvc.perform(put("/api/departments/" + d.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Updated Name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteDepartment_ShouldReturn200_AndSoftDelete() throws Exception {
        Department d = departmentRepository.save(Department.builder().code("D5").name("Name 5").isActive(true).build());

        mockMvc.perform(delete("/api/departments/" + d.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        entityManager.flush();
        entityManager.clear();
        assertFalse(departmentRepository.findById(d.getId()).isPresent());
    }
}
