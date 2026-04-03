package com.edu.university.modules.hr.service;

import com.edu.university.modules.hr.dto.request.PositionRequestDTO;
import com.edu.university.modules.hr.dto.response.PositionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface PositionService {
    PositionResponseDTO createPosition(PositionRequestDTO requestDTO);
    List<PositionResponseDTO> getAllPositions();
    PositionResponseDTO getPositionById(UUID id);
    PositionResponseDTO getPositionByCode(String code);
    PositionResponseDTO updatePosition(UUID id, PositionRequestDTO requestDTO);
    void deletePosition(UUID id);
}
