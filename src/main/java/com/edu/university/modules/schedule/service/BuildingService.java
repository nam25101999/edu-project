package com.edu.university.modules.schedule.service;

import com.edu.university.modules.schedule.dto.request.BuildingRequestDTO;
import com.edu.university.modules.schedule.dto.response.BuildingResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BuildingService {
    BuildingResponseDTO create(BuildingRequestDTO requestDTO);
    Page<BuildingResponseDTO> getAll(Pageable pageable);
    BuildingResponseDTO getById(UUID id);
    BuildingResponseDTO update(UUID id, BuildingRequestDTO requestDTO);
    void delete(UUID id);
}
