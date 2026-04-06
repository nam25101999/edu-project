package com.edu.university.modules.student.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.hr.entity.Employee;
import com.edu.university.modules.hr.repository.EmployeeRepository;
import com.edu.university.modules.student.dto.request.AdvisorClassSectionRequestDTO;
import com.edu.university.modules.student.entity.AdvisorClassSection;
import com.edu.university.modules.student.entity.StudentClass;
import com.edu.university.modules.student.repository.AdvisorClassSectionRepository;
import com.edu.university.modules.student.repository.StudentClassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdvisorClassSectionControllerIT extends BaseIntegrationTest {

    @Autowired
    private AdvisorClassSectionRepository advisorClassSectionRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private StudentClassRepository studentClassRepository;

    private Employee testAdvisor;
    private StudentClass testClass;

    @BeforeEach
    void setUp() {
        advisorClassSectionRepository.deleteAll();
        studentClassRepository.deleteAll();
        employeeRepository.deleteAll();

        // Create Advisor (Employee)
        testAdvisor = Employee.builder()
                .employeeCode("EMP001")
                .fullName("Cố vấn A")
                .isActive(true)
                .build();
        testAdvisor = employeeRepository.save(testAdvisor);

        // Create StudentClass
        testClass = StudentClass.builder()
                .classCode("CLASS001")
                .className("Lớp Công nghệ thông tin 1")
                .isActive(true)
                .build();
        testClass = studentClassRepository.save(testClass);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignAdvisorToClass_Success() throws Exception {
        AdvisorClassSectionRequestDTO request = new AdvisorClassSectionRequestDTO();
        request.setAdvisorId(testAdvisor.getId());
        request.setStudentClassesId(testClass.getId());
        request.setStartDate(LocalDate.now());

        mockMvc.perform(post("/api/advisor-class-sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.advisorName").value("Cố vấn A"))
                .andExpect(jsonPath("$.data.className").value("Lớp Công nghệ thông tin 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllAdvisorClassSections_Success() throws Exception {
        AdvisorClassSection section = AdvisorClassSection.builder()
                .advisor(testAdvisor)
                .studentClass(testClass)
                .startDate(LocalDate.now().atStartOfDay())
                .isActive(true)
                .build();
        advisorClassSectionRepository.save(section);

        mockMvc.perform(get("/api/advisor-class-sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].advisorName").value("Cố vấn A"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdvisorClassSectionByAdvisor_Success() throws Exception {
        AdvisorClassSection section = AdvisorClassSection.builder()
                .advisor(testAdvisor)
                .studentClass(testClass)
                .startDate(LocalDate.now().atStartOfDay())
                .isActive(true)
                .build();
        AdvisorClassSection saved = advisorClassSectionRepository.save(section);

        mockMvc.perform(get("/api/advisor-class-sections/advisor/{advisorId}", testAdvisor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(saved.getId().toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAdvisorClassSection_Success() throws Exception {
        AdvisorClassSection section = AdvisorClassSection.builder()
                .advisor(testAdvisor)
                .studentClass(testClass)
                .startDate(LocalDate.now().atStartOfDay())
                .isActive(true)
                .build();
        AdvisorClassSection saved = advisorClassSectionRepository.save(section);

        AdvisorClassSectionRequestDTO request = new AdvisorClassSectionRequestDTO();
        request.setAdvisorId(testAdvisor.getId());
        request.setStudentClassesId(testClass.getId());
        request.setStartDate(LocalDate.now().plusDays(1));

        mockMvc.perform(put("/api/advisor-class-sections/{id}", saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.startDate").value(LocalDate.now().plusDays(1).toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAdvisorClassSection_Success() throws Exception {
        AdvisorClassSection section = AdvisorClassSection.builder()
                .advisor(testAdvisor)
                .studentClass(testClass)
                .startDate(LocalDate.now().atStartOfDay())
                .isActive(true)
                .build();
        AdvisorClassSection saved = advisorClassSectionRepository.save(section);

        mockMvc.perform(delete("/api/advisor-class-sections/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/advisor-class-sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(0));
    }
}
