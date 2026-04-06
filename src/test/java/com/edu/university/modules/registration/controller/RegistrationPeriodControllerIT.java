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
 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
 
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 
public class RegistrationPeriodControllerIT extends BaseIntegrationTest {
 
    @Autowired
    private RegistrationPeriodRepository registrationPeriodRepository;
 
    @Autowired
    private SemesterRepository semesterRepository;
 
    private Semester semester;
    private RegistrationPeriod registrationPeriod;
 
    @BeforeEach
    void setUp() {
        registrationPeriodRepository.deleteAll();
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
 
        registrationPeriod = RegistrationPeriod.builder()
                .name("Đợt đăng ký chính thức HK1 2023")
                .semester(semester)
                .startTime(LocalDateTime.of(2023, 8, 15, 8, 0))
                .endTime(LocalDateTime.of(2023, 8, 30, 17, 0))
                .maxCredits(25)
                .minCredits(12)
                .allowRetake(true)
                .isActive(true)
                .build();
        registrationPeriod = registrationPeriodRepository.save(registrationPeriod);
    }
 
    @Test
    @WithMockUser(roles = "ADMIN")
    void createRegistrationPeriod_Success() throws Exception {
        RegistrationPeriodRequestDTO requestDTO = new RegistrationPeriodRequestDTO();
        requestDTO.setName("Đợt đăng ký bổ sung HK1 2023");
        requestDTO.setSemesterId(semester.getId());
        requestDTO.setStartTime(LocalDateTime.of(2023, 9, 1, 8, 0));
        requestDTO.setEndTime(LocalDateTime.of(2023, 9, 5, 17, 0));
        requestDTO.setMaxCredits(25);
        requestDTO.setMinCredits(12);
        requestDTO.setAllowRetake(true);
 
        mockMvc.perform(post("/api/registration-periods")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Đợt đăng ký bổ sung HK1 2023"))
                .andExpect(jsonPath("$.semesterId").value(semester.getId().toString()));
    }
 
    @Test
    @WithMockUser(roles = "USER")
    void getAllRegistrationPeriods_Success() throws Exception {
        mockMvc.perform(get("/api/registration-periods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
 
    @Test
    @WithMockUser(roles = "USER")
    void getRegistrationPeriodById_Success() throws Exception {
        mockMvc.perform(get("/api/registration-periods/{id}", registrationPeriod.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Đợt đăng ký chính thức HK1 2023"));
    }
 
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRegistrationPeriod_Success() throws Exception {
        RegistrationPeriodRequestDTO requestDTO = new RegistrationPeriodRequestDTO();
        requestDTO.setName("Đợt đăng ký chính thức HK1 2023 - Updated");
        requestDTO.setSemesterId(semester.getId());
        requestDTO.setStartTime(registrationPeriod.getStartTime());
        requestDTO.setEndTime(registrationPeriod.getEndTime());
 
        mockMvc.perform(put("/api/registration-periods/{id}", registrationPeriod.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Đợt đăng ký chính thức HK1 2023 - Updated"));
    }
 
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRegistrationPeriod_Success() throws Exception {
        mockMvc.perform(delete("/api/registration-periods/{id}", registrationPeriod.getId()))
                .andExpect(status().isNoContent());
 
        entityManager.flush();
        entityManager.clear();
 
        mockMvc.perform(get("/api/registration-periods/{id}", registrationPeriod.getId()))
                .andExpect(status().isNotFound());
    }
}
