package com.edu.university.modules.finance.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.repository.MajorRepository;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.hr.entity.Faculty;
import com.edu.university.modules.hr.repository.FacultyRepository;
import com.edu.university.modules.finance.dto.request.TuitionFeeRequestDTO;
import com.edu.university.modules.finance.entity.TuitionFee;
import com.edu.university.modules.finance.repository.TuitionFeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TuitionFeeControllerIT extends BaseIntegrationTest {

    @Autowired
    private TuitionFeeRepository tuitionFeeRepository;
    @Autowired
    private TrainingProgramRepository trainingProgramRepository;
    @Autowired
    private MajorRepository majorRepository;
    @Autowired
    private FacultyRepository facultyRepository;

    private TrainingProgram testProgram;

    @BeforeEach
    void setUp() {
        tuitionFeeRepository.deleteAll();
        trainingProgramRepository.deleteAll();
        majorRepository.deleteAll();
        facultyRepository.deleteAll();

        Faculty faculty = facultyRepository.save(Faculty.builder()
                .code("IT")
                .name("Information Technology")
                .build());

        Major major = majorRepository.save(Major.builder()
                .majorCode("IT_F")
                .name("Information Technology")
                .faculty(faculty)
                .isActive(true)
                .build());

        testProgram = trainingProgramRepository.save(TrainingProgram.builder()
                .programName("IT Standard Program")
                .major(major)
                .isActive(true)
                .build());
    }

    @Test
    void getAll_ShouldReturn401_WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/tuition-fees"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void create_ShouldReturn201_WhenValidRequest() throws Exception {
        TuitionFeeRequestDTO request = new TuitionFeeRequestDTO();
        request.setTrainingProgramId(testProgram.getId());
        request.setCourseYear("2023");
        request.setPricePerCredit(new BigDecimal("500000"));
        request.setBaseTuition(new BigDecimal("10000000"));
        request.setEffectiveDate(LocalDate.now());

        mockMvc.perform(post("/api/tuition-fees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.courseYear").value("2023"))
                .andExpect(jsonPath("$.data.pricePerCredit").value(500000.0));
    }

    @Test
    @WithMockUser
    void getAll_ShouldReturnPaginationMetadata() throws Exception {
        saveTuitionFee("2022", new BigDecimal("450000"));
        saveTuitionFee("2023", new BigDecimal("500000"));

        mockMvc.perform(get("/api/tuition-fees")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));
    }

    @Test
    @WithMockUser
    void getById_ShouldReturnTuitionFee_WhenExists() throws Exception {
        TuitionFee fee = saveTuitionFee("2023", new BigDecimal("500000"));

        mockMvc.perform(get("/api/tuition-fees/" + fee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.courseYear").value("2023"));
    }

    @Test
    @WithMockUser
    void update_ShouldReturn200_WhenValidRequest() throws Exception {
        TuitionFee fee = saveTuitionFee("2023", new BigDecimal("500000"));

        TuitionFeeRequestDTO request = new TuitionFeeRequestDTO();
        request.setTrainingProgramId(testProgram.getId());
        request.setCourseYear("2023");
        request.setPricePerCredit(new BigDecimal("550000"));

        mockMvc.perform(put("/api/tuition-fees/" + fee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pricePerCredit").value(550000.0));
    }

    @Test
    @WithMockUser
    void delete_ShouldSoftDelete_WhenExists() throws Exception {
        TuitionFee fee = saveTuitionFee("2023", new BigDecimal("500000"));

        mockMvc.perform(delete("/api/tuition-fees/" + fee.getId()))
                .andExpect(status().isOk());

        TuitionFee deleted = tuitionFeeRepository.findById(fee.getId()).orElse(null);
        org.junit.jupiter.api.Assertions.assertNotNull(deleted);
        org.junit.jupiter.api.Assertions.assertFalse(deleted.isActive());
    }

    private TuitionFee saveTuitionFee(String year, BigDecimal price) {
        TuitionFee fee = new TuitionFee();
        fee.setTrainingProgram(testProgram);
        fee.setCourseYear(year);
        fee.setPricePerCredit(price);
        fee.setBaseTuition(new BigDecimal("10000000"));
        fee.setEffectiveDate(LocalDate.now());
        fee.setActive(true);
        return tuitionFeeRepository.save(fee);
    }
}
