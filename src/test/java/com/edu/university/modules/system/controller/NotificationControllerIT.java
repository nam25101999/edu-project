package com.edu.university.modules.system.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.repository.RoleRepository;
import com.edu.university.modules.system.dto.request.NotificationRequestDTO;
import com.edu.university.modules.system.entity.Notification;
import com.edu.university.modules.system.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class NotificationControllerIT extends BaseIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role adminRole;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        // RoleRepository might have data from data seeder, but for isolation let's ensure we have a role
        adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name("ROLE_ADMIN")
                        .description("Admin Role")
                        .build()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn201_WhenValidRequest() throws Exception {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setTitle("System Update");
        request.setContent("Scheduled maintenance at midnight.");
        request.setTargetRoleId(adminRole.getId());

        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.title").value("System Update"))
                .andExpect(jsonPath("$.data.targetRoleName").value("ROLE_ADMIN"));
        
        assertEquals(1, notificationRepository.count());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ShouldReturnListOfNotifications() throws Exception {
        Notification n = Notification.builder()
                .title("N1")
                .content("C1")
                .isActive(true)
                .build();
        notificationRepository.save(n);

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("N1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn204_WhenNotificationExists() throws Exception {
        Notification n = Notification.builder()
                .title("To Delete")
                .content("Content")
                .isActive(true)
                .build();
        Notification saved = notificationRepository.save(n);

        mockMvc.perform(delete("/api/notifications/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify soft delete (BaseIntegrationTest has EntityManager to clear cache)
        entityManager.flush();
        entityManager.clear();
        
        assertFalse(notificationRepository.findById(saved.getId()).isPresent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn404_WhenNotificationNotExists() throws Exception {
        mockMvc.perform(delete("/api/notifications/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
