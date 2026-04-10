package com.edu.university.modules.schedule.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.schedule.dto.request.RoomRequestDTO;
import com.edu.university.modules.schedule.entity.Building;
import com.edu.university.modules.schedule.entity.Room;
import com.edu.university.modules.schedule.repository.BuildingRepository;
import com.edu.university.modules.schedule.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RoomControllerIT extends BaseIntegrationTest {

        @Autowired
        private RoomRepository roomRepository;

        @Autowired
        private BuildingRepository buildingRepository;

        private Building building;
        private Room room;

        @BeforeEach
        void setUp() {
                roomRepository.deleteAll();
                buildingRepository.deleteAll();

                building = Building.builder()
                                .buildingCode("B1")
                                .buildingName("Building 1")
                                .isActive(true)
                                .build();
                building = buildingRepository.save(building);

                room = Room.builder()
                                .roomCode("R101")
                                .roomName("Room 101")
                                .building(building)
                                .roomType("Theory")
                                .capacity(50)
                                .isActive(true)
                                .build();
                room = roomRepository.save(room);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createRoom_Success() throws Exception {
                RoomRequestDTO request = new RoomRequestDTO();
                request.setRoomCode("R102");
                request.setRoomName("Room 102");
                request.setBuildingId(building.getId());
                request.setRoomType("Practice");
                request.setCapacity(30);

                mockMvc.perform(post("/api/rooms")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.roomCode").value("R102"))
                                .andExpect(jsonPath("$.data.buildingName").value("Building 1"));
        }

        @Test
        @WithMockUser(roles = "USER")
        void getAllRooms_Success() throws Exception {
                mockMvc.perform(get("/api/rooms"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @WithMockUser(roles = "USER")
        void getRoomById_Success() throws Exception {
                mockMvc.perform(get("/api/rooms/{id}", room.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.roomCode").value("R101"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateRoom_Success() throws Exception {
                RoomRequestDTO request = new RoomRequestDTO();
                request.setRoomCode("R101_UPDATED");
                request.setRoomName("Room 101 Updated");
                request.setBuildingId(building.getId());
                request.setCapacity(60);

                mockMvc.perform(put("/api/rooms/{id}", room.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.roomCode").value("R101_UPDATED"))
                                .andExpect(jsonPath("$.data.capacity").value(60));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void deleteRoom_Success() throws Exception {
                mockMvc.perform(delete("/api/rooms/{id}", room.getId()))
                                .andExpect(status().isNoContent());

                entityManager.flush();
                entityManager.clear();

                mockMvc.perform(get("/api/rooms/{id}", room.getId()))
                                .andExpect(status().isNotFound());
        }
}
