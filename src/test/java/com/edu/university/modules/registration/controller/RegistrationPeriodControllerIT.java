package com.edu.university.modules.registration.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.registration.dto.request.RegistrationPeriodRequestDTO;
import com.edu.university.modules.registration.entity.RegistrationPeriod;
import com.edu.university.modules.registration.repository.RegistrationPeriodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RegistrationPeriodControllerIT extends BaseIntegrationTest {

    @Autowired
    private RegistrationPeriodRepository registrationPeriodRepository;
    @Autowired
    private SemesterRepository semesterRepository;

    private Semester testSemester;

    @BeforeEach
    void setUp() {
        registrationPeriodRepository.deleteAll();
        testSemester = semesterRepository.save(Semester.builder()
                .semesterName("HK2 2024-2025")
                .semesterCode("20242_PERIOD")
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn201_WhenValidRequest() throws Exception {
        RegistrationPeriodRequestDTO request = new RegistrationPeriodRequestDTO();
        request.setName("HK2 Period 1");
        request.setSemesterId(testSemester.getId());
        request.setStartTime(LocalDateTime.now().plusDays(1));
        request.setEndTime(LocalDateTime.now().plusDays(10));
        request.setMinCredits(12);
        request.setMaxCredits(25);

        mockMvc.perform(post("/api/registration-periods")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("HK2 Period 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ShouldReturnPage() throws Exception {
        RegistrationPeriod period = RegistrationPeriod.builder()
                .name("HK2 Period 2")
                .semester(testSemester)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(10))
                .minCredits(12)
                .maxCredits(25)
                .isActive(true)
                .build();
        registrationPeriodRepository.save(period);

        mockMvc.perform(get("/api/registration-periods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn200_WhenExists() throws Exception {
        RegistrationPeriod period = RegistrationPeriod.builder()
                .name("To Delete")
                .semester(testSemester)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(10))
                .minCredits(12)
                .maxCredits(25)
                .isActive(true)
                .build();
        RegistrationPeriod saved = registrationPeriodRepository.save(period);

        mockMvc.perform(delete("/api/registration-periods/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa đợt đăng ký thành công"));

        RegistrationPeriod deleted = registrationPeriodRepository.findById(saved.getId()).orElse(null);
        org.junit.jupiter.api.Assertions.assertNotNull(deleted);
        org.junit.jupiter.api.Assertions.assertFalse(deleted.isActive());
    }
}
