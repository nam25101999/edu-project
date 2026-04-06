package com.edu.university.modules.system.controller;

import com.edu.university.BackendApplication;
import com.edu.university.BaseIntegrationTest;
import com.edu.university.builders.UsersBuilder;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.system.dto.request.UserNotificationRequestDTO;
import com.edu.university.modules.system.entity.Notification;
import com.edu.university.modules.system.entity.UserNotification;
import com.edu.university.modules.system.repository.NotificationRepository;
import com.edu.university.modules.system.repository.UserNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserNotificationControllerIT extends BaseIntegrationTest {

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private Users testUser;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        userNotificationRepository.deleteAll();
        
        testUser = userRepository.save(UsersBuilder.aUser()
                .withUsername("testuser_" + UUID.randomUUID())
                .build());
                
        testNotification = notificationRepository.save(Notification.builder()
                .title("System Alert")
                .content("Test content")
                .isActive(true)
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn201_WhenValidRequest() throws Exception {
        UserNotificationRequestDTO request = new UserNotificationRequestDTO();
        request.setUserId(testUser.getId());
        request.setNotificationId(testNotification.getId());

        mockMvc.perform(post("/api/user-notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.notificationTitle").value("System Alert"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByUserId_ShouldReturnListOfUserNotifications() throws Exception {
        UserNotification un = UserNotification.builder()
                .user(testUser)
                .notification(testNotification)
                .isRead(false)
                .isActive(true)
                .build();
        userNotificationRepository.save(un);

        mockMvc.perform(get("/api/user-notifications/user/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].read").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void markAsRead_ShouldReturn204_WhenNotificationExists() throws Exception {
        UserNotification un = UserNotification.builder()
                .user(testUser)
                .notification(testNotification)
                .isRead(false)
                .isActive(true)
                .build();
        UserNotification saved = userNotificationRepository.save(un);

        mockMvc.perform(patch("/api/user-notifications/" + saved.getId() + "/read"))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();

        UserNotification updated = userNotificationRepository.findById(saved.getId()).orElseThrow();
        assertTrue(updated.isRead());
        assertNotNull(updated.getReadAt());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void markAsRead_ShouldReturn404_WhenNotificationNotExists() throws Exception {
        mockMvc.perform(patch("/api/user-notifications/" + UUID.randomUUID() + "/read"))
                .andExpect(status().isNotFound());
    }
}
