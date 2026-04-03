package com.edu.university.modules.student.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.student.dto.request.StudentRequestDTO;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StudentControllerIT extends BaseIntegrationTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    private Users testUser;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        userRepository.deleteAll();

        testUser = Users.builder()
                .username("student_user")
                .password("password")
                .email("student@example.com")
                .isActive(true)
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateStudentSuccess() throws Exception {
        StudentRequestDTO request = new StudentRequestDTO();
        request.setStudentCode("STU001");
        request.setUserId(testUser.getId());
        request.setFirstName("Văn A");
        request.setLastName("Nguyễn");
        request.setEmail("vana@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentCode").value("STU001"))
                .andExpect(jsonPath("$.fullName").value("Văn A Nguyễn"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllStudents() throws Exception {
        Student s1 = Student.builder()
                .studentCode("STU001")
                .fullName("Nguyễn Văn A")
                .user(testUser)
                .isActive(true)
                .build();
        studentRepository.save(s1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].studentCode").value("STU001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStudentById() throws Exception {
        Student student = Student.builder()
                .studentCode("STU001")
                .fullName("Nguyễn Văn A")
                .user(testUser)
                .isActive(true)
                .build();
        student = studentRepository.save(student);
        UUID id = student.getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/students/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentCode").value("STU001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStudent() throws Exception {
        Student student = Student.builder()
                .studentCode("STU001")
                .fullName("Old Name")
                .user(testUser)
                .isActive(true)
                .build();
        student = studentRepository.save(student);
        UUID id = student.getId();

        StudentRequestDTO updateRequest = new StudentRequestDTO();
        updateRequest.setStudentCode("STU001");
        updateRequest.setUserId(testUser.getId());
        updateRequest.setFirstName("Văn B");
        updateRequest.setLastName("Nguyễn");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/students/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Văn B Nguyễn"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteStudent() throws Exception {
        Student student = Student.builder()
                .studentCode("STU001")
                .fullName("To Delete")
                .user(testUser)
                .isActive(true)
                .build();
        student = studentRepository.save(student);
        UUID id = student.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/students/" + id))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/students/" + id))
                .andExpect(status().isNotFound());
    }
}
