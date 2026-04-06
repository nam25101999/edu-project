package com.edu.university.modules.system.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.builders.UsersBuilder;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.system.entity.SystemLog;
import com.edu.university.modules.system.repository.SystemLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SystemLogControllerIT extends BaseIntegrationTest {

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Autowired
    private UserRepository userRepository;

    private Users testUser;

    @BeforeEach
    void setUp() {
        systemLogRepository.deleteAll();
        
        testUser = userRepository.save(UsersBuilder.aUser()
                .withUsername("loguser_" + UUID.randomUUID())
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ShouldReturnListOfLogs() throws Exception {
        SystemLog log = SystemLog.builder()
                .user(testUser)
                .action("LOGIN")
                .tableName("users")
                .recordId(testUser.getId())
                .description("User logged in")
                .isActive(true)
                .build();
        systemLogRepository.save(log);

        mockMvc.perform(get("/api/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].action").value("LOGIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByUserId_ShouldReturnLogsForSpecificUser() throws Exception {
        SystemLog log = SystemLog.builder()
                .user(testUser)
                .action("UPDATE")
                .tableName("settings")
                .recordId(UUID.randomUUID())
                .description("Updated setting")
                .isActive(true)
                .build();
        systemLogRepository.save(log);

        mockMvc.perform(get("/api/logs/user/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].action").value("UPDATE"));
    }
}
