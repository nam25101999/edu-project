package com.edu.university.modules.schedule.controller;
 
import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.schedule.dto.request.BuildingRequestDTO;
import com.edu.university.modules.schedule.entity.Building;
import com.edu.university.modules.schedule.repository.BuildingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
 
import java.util.UUID;
 
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 
public class BuildingControllerIT extends BaseIntegrationTest {
 
    @Autowired
    private BuildingRepository buildingRepository;
 
    private Building building;
 
    @BeforeEach
    void setUp() {
        buildingRepository.deleteAll();
        building = Building.builder()
                .buildingCode("B1")
                .buildingName("Tòa nhà C1")
                .isActive(true)
                .build();
        building = buildingRepository.save(building);
    }
 
    @Test
    @WithMockUser(roles = "ADMIN")
    void createBuilding_Success() throws Exception {
        BuildingRequestDTO request = new BuildingRequestDTO();
        request.setBuildingCode("B2");
        request.setBuildingName("Tòa nhà C2");
 
        mockMvc.perform(post("/api/buildings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.buildingCode").value("B2"))
                .andExpect(jsonPath("$.data.buildingName").value("Tòa nhà C2"));
    }
 
    @Test
    @WithMockUser(roles = "USER")
    void getAllBuildings_Success() throws Exception {
        mockMvc.perform(get("/api/buildings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }
 
    @Test
    @WithMockUser(roles = "USER")
    void getBuildingById_Success() throws Exception {
        mockMvc.perform(get("/api/buildings/{id}", building.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.buildingCode").value("B1"));
    }
 
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBuilding_Success() throws Exception {
        BuildingRequestDTO request = new BuildingRequestDTO();
        request.setBuildingCode("B1_UPDATED");
        request.setBuildingName("Tòa nhà C1 Updated");
 
        mockMvc.perform(put("/api/buildings/{id}", building.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.buildingCode").value("B1_UPDATED"));
    }
 
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBuilding_Success() throws Exception {
        mockMvc.perform(delete("/api/buildings/{id}", building.getId()))
                .andExpect(status().isOk());
 
        entityManager.flush();
        entityManager.clear();
 
        mockMvc.perform(get("/api/buildings/{id}", building.getId()))
                .andExpect(status().isNotFound());
    }
}
