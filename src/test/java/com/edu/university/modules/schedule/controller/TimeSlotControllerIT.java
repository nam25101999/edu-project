package com.edu.university.modules.schedule.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.schedule.dto.request.TimeSlotRequestDTO;
import com.edu.university.modules.schedule.entity.TimeSlot;
import com.edu.university.modules.schedule.repository.TimeSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TimeSlotControllerIT extends BaseIntegrationTest {

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    private TimeSlot timeSlot;

    @BeforeEach
    void setUp() {
        timeSlotRepository.deleteAll();
        timeSlot = TimeSlot.builder()
                .slotCode("S1")
                .slotName("Tiết 1-3")
                .startTime(LocalTime.of(7, 0))
                .endTime(LocalTime.of(9, 30))
                .isActive(true)
                .build();
        timeSlot = timeSlotRepository.save(timeSlot);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTimeSlot_Success() throws Exception {
        TimeSlotRequestDTO request = new TimeSlotRequestDTO();
        request.setSlotCode("S2");
        request.setSlotName("Tiết 4-6");
        request.setStartTime(LocalTime.of(9, 45));
        request.setEndTime(LocalTime.of(12, 15));

        mockMvc.perform(post("/api/time-slots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slotCode").value("S2"))
                .andExpect(jsonPath("$.slotName").value("Tiết 4-6"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllTimeSlots_Success() throws Exception {
        mockMvc.perform(get("/api/time-slots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTimeSlotById_Success() throws Exception {
        mockMvc.perform(get("/api/time-slots/{id}", timeSlot.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slotCode").value("S1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateTimeSlot_Success() throws Exception {
        TimeSlotRequestDTO request = new TimeSlotRequestDTO();
        request.setSlotCode("S1_UPDATED");
        request.setSlotName("Tiết 1-3 Updated");
        request.setStartTime(LocalTime.of(7, 15));
        request.setEndTime(LocalTime.of(9, 45));

        mockMvc.perform(put("/api/time-slots/{id}", timeSlot.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slotCode").value("S1_UPDATED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTimeSlot_Success() throws Exception {
        mockMvc.perform(delete("/api/time-slots/{id}", timeSlot.getId()))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/time-slots/{id}", timeSlot.getId()))
                .andExpect(status().isNotFound());
    }
}
