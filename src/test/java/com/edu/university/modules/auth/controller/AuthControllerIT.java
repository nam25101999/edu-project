package com.edu.university.modules.auth.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.auth.dto.AuthDtos;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.RoleRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerIT extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role adminRole = Role.builder()
                .name("ROLE_ADMIN")
                .description("Admin role")
                .permissions(new HashSet<>())
                .build();
        roleRepository.save(adminRole);

        Users adminUser = Users.builder()
                .username("admin_test")
                .password(passwordEncoder.encode("password123"))
                .email("admin_test@example.com")
                .roles(new HashSet<>(Set.of(adminRole)))
                .isActive(true)
                .emailVerified(true)
                .build();
        userRepository.save(adminUser);
    }

    @Test
    void testLoginSuccess() throws Exception {
        AuthDtos.LoginRequest loginRequest = new AuthDtos.LoginRequest("admin_test", "password123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.user.username").value("admin_test"));
    }

    @Test
    void testLoginWithWrongPassword() throws Exception {
        AuthDtos.LoginRequest loginRequest = new AuthDtos.LoginRequest("admin_test", "wrong_password");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginWithNonExistentUser() throws Exception {
        AuthDtos.LoginRequest loginRequest = new AuthDtos.LoginRequest("ghost", "password123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound());
    }
}
