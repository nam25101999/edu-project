package com.edu.university.modules.schedule.service;

import com.edu.university.modules.schedule.dto.request.BuildingRequestDTO;
import com.edu.university.modules.schedule.dto.response.BuildingResponseDTO;

import java.util.List;
import java.util.UUID;

public interface BuildingService {
    BuildingResponseDTO create(BuildingRequestDTO requestDTO);
    List<BuildingResponseDTO> getAll();
    BuildingResponseDTO getById(UUID id);
    BuildingResponseDTO update(UUID id, BuildingRequestDTO requestDTO);
    void delete(UUID id);
}
