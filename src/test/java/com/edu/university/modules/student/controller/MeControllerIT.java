package com.edu.university.modules.student.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.auth.dto.AuthDtos;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.RoleRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MeControllerIT extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String studentAccessToken;

    @BeforeEach
    void setUp() throws Exception {
        studentRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // 1. Tạo Role STUDENT
        Role studentRole = Role.builder()
                .name("ROLE_STUDENT")
                .description("Student role")
                .permissions(new HashSet<>())
                .build();
        roleRepository.save(studentRole);

        // 2. Tạo User STUDENT
        Users studentUser = Users.builder()
                .username("student_test")
                .password(passwordEncoder.encode("password123"))
                .email("student_test@example.com")
                .roles(new HashSet<>(Set.of(studentRole)))
                .isActive(true)
                .emailVerified(true)
                .build();
        userRepository.save(studentUser);

        // 3. Tạo Profile Student
        Student studentProfile = Student.builder()
                .studentCode("ST001")
                .fullName("Test Student")
                .gender("1")
                .user(studentUser)
                .isActive(true)
                .build();
        studentRepository.save(studentProfile);

        // 4. Đăng nhập để lấy Token
        AuthDtos.LoginRequest loginRequest = new AuthDtos.LoginRequest("student_test", "password123");

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        JsonNode responseNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        studentAccessToken = responseNode.get("data").get("accessToken").asText();
    }

    @Test
    void testGetMyProfileSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/me/profile")
                .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.studentCode").value("ST001"))
                .andExpect(jsonPath("$.data.fullName").value("Test Student"));
    }

    @Test
    void testGetMyProfileUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/me/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetMyScheduleWhenEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/me/schedule")
                .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }
}
