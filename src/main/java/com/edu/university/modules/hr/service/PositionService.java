package com.edu.university.modules.hr.service;

import com.edu.university.modules.hr.dto.request.PositionRequestDTO;
import com.edu.university.modules.hr.dto.response.PositionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PositionService {
    PositionResponseDTO createPosition(PositionRequestDTO requestDTO);
    Page<PositionResponseDTO> getAllPositions(Pageable pageable);
    PositionResponseDTO getPositionById(UUID id);
    PositionResponseDTO getPositionByCode(String code);
    PositionResponseDTO updatePosition(UUID id, PositionRequestDTO requestDTO);
    void deletePosition(UUID id);
}
