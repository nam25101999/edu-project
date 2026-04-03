package com.edu.university.modules.curriculum.controller;

import com.edu.university.BackendApplication;
import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.curriculum.dto.request.TrainingProgramRequestDTO;
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.repository.MajorRepository;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class TrainingProgramControllerIT extends BaseIntegrationTest {

    @Autowired
    private TrainingProgramRepository trainingProgramRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Major testMajor;
    private Department testDept;

    @BeforeEach
    void setUp() {
        trainingProgramRepository.deleteAll();
        majorRepository.deleteAll();
        departmentRepository.deleteAll();
        
        testDept = departmentRepository.save(Department.builder()
                .code("DEPT_TP")
                .name("TP Department")
                .isActive(true)
                .build());

        testMajor = majorRepository.save(Major.builder()
                .code("MAJOR_TP")
                .name("TP Major")
                .isActive(true)
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn201_WhenValid() throws Exception {
        TrainingProgramRequestDTO request = new TrainingProgramRequestDTO();
        request.setProgramCode("PROG_01");
        request.setProgramName("Computer Science Program");
        request.setMajorId(testMajor.getId());
        request.setDepartmentId(testDept.getId());
        request.setTotalCredits(new BigDecimal("130"));

        mockMvc.perform(post("/api/training-programs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.programCode").value("PROG_01"))
                .andExpect(jsonPath("$.major.code").value("MAJOR_TP"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn409_WhenCodeExists() throws Exception {
        trainingProgramRepository.save(TrainingProgram.builder().programCode("PROG_01").programName("Old").isActive(true).build());

        TrainingProgramRequestDTO request = new TrainingProgramRequestDTO();
        request.setProgramCode("PROG_01");
        request.setProgramName("New");

        mockMvc.perform(post("/api/training-programs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ShouldReturnList() throws Exception {
        trainingProgramRepository.save(TrainingProgram.builder().programCode("P1").programName("Prog 1").isActive(true).build());
        
        mockMvc.perform(get("/api/training-programs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ShouldReturnProgram() throws Exception {
        TrainingProgram tp = trainingProgramRepository.save(TrainingProgram.builder().programCode("P2").programName("Prog 2").isActive(true).build());

        mockMvc.perform(get("/api/training-programs/" + tp.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.programCode").value("P2"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldReturnUpdated() throws Exception {
        TrainingProgram tp = trainingProgramRepository.save(TrainingProgram.builder().programCode("P3").programName("Prog 3").isActive(true).build());

        TrainingProgramRequestDTO request = new TrainingProgramRequestDTO();
        request.setProgramCode("P3");
        request.setProgramName("Updated Program");
        request.setMajorId(testMajor.getId());

        mockMvc.perform(put("/api/training-programs/" + tp.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.programName").value("Updated Program"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn204_AndSoftDelete() throws Exception {
        TrainingProgram tp = trainingProgramRepository.save(TrainingProgram.builder().programCode("P4").programName("Prog 4").isActive(true).build());

        mockMvc.perform(delete("/api/training-programs/" + tp.getId()))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();
        assertFalse(trainingProgramRepository.findById(tp.getId()).isPresent());
    }
}
