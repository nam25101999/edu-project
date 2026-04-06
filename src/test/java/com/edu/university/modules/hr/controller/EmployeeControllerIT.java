package com.edu.university.modules.hr.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.hr.dto.request.EmployeeRequestDTO;
import com.edu.university.modules.hr.entity.Employee;
import com.edu.university.modules.hr.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EmployeeControllerIT extends BaseIntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    private Users testUser;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        userRepository.deleteAll();

        testUser = Users.builder()
                .username("employee_user")
                .password("password")
                .email("employee@example.com")
                .isActive(true)
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateEmployeeSuccess() throws Exception {
        EmployeeRequestDTO request = new EmployeeRequestDTO();
        request.setEmployeeCode("EMP001");
        request.setFullName("Nguyễn Văn Employee");
        request.setEmail("emp001@example.com");
        request.setUserId(testUser.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.employeeCode").value("EMP001"))
                .andExpect(jsonPath("$.data.fullName").value("Nguyễn Văn Employee"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllEmployees() throws Exception {
        Employee emp = Employee.builder()
                .employeeCode("EMP001")
                .fullName("Employee 1")
                .isActive(true)
                .build();
        employeeRepository.save(emp);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].employeeCode").value("EMP001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetEmployeeById() throws Exception {
        Employee emp = Employee.builder()
                .employeeCode("EMP001")
                .fullName("Employee 1")
                .isActive(true)
                .build();
        emp = employeeRepository.save(emp);
        UUID id = emp.getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.employeeCode").value("EMP001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteEmployee() throws Exception {
        Employee emp = Employee.builder()
                .employeeCode("EMP001")
                .fullName("To Delete")
                .isActive(true)
                .build();
        emp = employeeRepository.save(emp);
        UUID id = emp.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/employees/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/" + id))
                .andExpect(status().isNotFound());
    }
}
