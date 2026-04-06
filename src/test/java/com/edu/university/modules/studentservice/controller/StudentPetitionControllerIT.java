package com.edu.university.modules.studentservice.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.studentservice.dto.request.PetitionProcessRequest;
import com.edu.university.modules.studentservice.dto.request.PetitionRequest;
import com.edu.university.modules.studentservice.entity.StudentPetition;
import com.edu.university.modules.studentservice.repository.StudentPetitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class StudentPetitionControllerIT extends BaseIntegrationTest {

    @Autowired
    private StudentPetitionRepository petitionRepository;

    @Autowired
    private StudentRepository studentRepository;

    private Student student;
    private StudentPetition petition;

    @BeforeEach
    void setUp() {
        petitionRepository.deleteAll();
        
        student = new Student();
        student.setStudentCode("S_PET_001");
        student.setFullName("Petition Student");
        student = studentRepository.save(student);

        petition = StudentPetition.builder()
                .student(student)
                .title("Đơn xin nghỉ học tạm thời")
                .content("Lý do cá nhân")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        petition = petitionRepository.save(petition);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void createPetition_Success() throws Exception {
        PetitionRequest request = new PetitionRequest();
        request.setStudentId(student.getId());
        request.setTitle("Đơn phúc khảo");
        request.setContent("Phúc khảo môn Toán");

        mockMvc.perform(post("/api/petitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Đơn phúc khảo"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getPetitionsByStudent_Success() throws Exception {
        mockMvc.perform(get("/api/petitions/student/{studentId}", student.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void processPetition_Success() throws Exception {
        PetitionProcessRequest request = new PetitionProcessRequest();
        request.setStatus("APPROVED");
        request.setResponseContent("Đã chấp nhận đơn");

        mockMvc.perform(put("/api/petitions/{id}/process", petition.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }
}
