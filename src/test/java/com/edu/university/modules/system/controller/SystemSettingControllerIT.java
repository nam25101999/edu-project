package com.edu.university.modules.system.controller;

import com.edu.university.BackendApplication;
import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.system.dto.request.SystemSettingRequestDTO;
import com.edu.university.modules.system.entity.SystemSetting;
import com.edu.university.modules.system.repository.SystemSettingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SystemSettingControllerIT extends BaseIntegrationTest {

    @Autowired
    private SystemSettingRepository systemSettingRepository;

    @BeforeEach
    void setUp() {
        systemSettingRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldCreateNewSetting_WhenKeyNotExists() throws Exception {
        SystemSettingRequestDTO request = new SystemSettingRequestDTO();
        request.setKey("site_title");
        request.setValue("My University");
        request.setDescription("Title of the website");

        mockMvc.perform(post("/api/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("site_title"))
                .andExpect(jsonPath("$.value").value("My University"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldUpdateExistingSetting_WhenKeyExists() throws Exception {
        SystemSetting existing = SystemSetting.builder()
                .key("site_title")
                .value("Old Title")
                .isActive(true)
                .build();
        systemSettingRepository.save(existing);

        SystemSettingRequestDTO request = new SystemSettingRequestDTO();
        request.setKey("site_title");
        request.setValue("New Title");

        mockMvc.perform(post("/api/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("New Title"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ShouldReturnListOfSettings() throws Exception {
        SystemSetting s1 = SystemSetting.builder().key("k1").value("v1").isActive(true).build();
        SystemSetting s2 = SystemSetting.builder().key("k2").value("v2").isActive(true).build();
        systemSettingRepository.save(s1);
        systemSettingRepository.save(s2);

        mockMvc.perform(get("/api/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByKey_ShouldReturnSetting_WhenKeyExists() throws Exception {
        SystemSetting setting = SystemSetting.builder().key("test_key").value("test_val").isActive(true).build();
        systemSettingRepository.save(setting);

        mockMvc.perform(get("/api/settings/test_key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("test_val"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByKey_ShouldReturn404_WhenKeyNotExists() throws Exception {
        mockMvc.perform(get("/api/settings/non_existent"))
                .andExpect(status().isNotFound());
    }
}
