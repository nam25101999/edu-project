package com.edu.university.modules.schedule.service;

import com.edu.university.modules.schedule.dto.request.RoomRequestDTO;
import com.edu.university.modules.schedule.dto.response.RoomResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RoomService {
    RoomResponseDTO create(RoomRequestDTO requestDTO);
    Page<RoomResponseDTO> getAll(Pageable pageable);
    RoomResponseDTO getById(UUID id);
    RoomResponseDTO update(UUID id, RoomRequestDTO requestDTO);
    void delete(UUID id);
}
