package com.edu.university.modules.schedule.service;

import com.edu.university.modules.schedule.dto.request.RoomRequestDTO;
import com.edu.university.modules.schedule.dto.response.RoomResponseDTO;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    RoomResponseDTO create(RoomRequestDTO requestDTO);
    List<RoomResponseDTO> getAll();
    RoomResponseDTO getById(UUID id);
    RoomResponseDTO update(UUID id, RoomRequestDTO requestDTO);
    void delete(UUID id);
}
