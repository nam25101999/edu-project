package com.edu.university.modules.schedule.service;

import com.edu.university.modules.schedule.dto.request.TimeSlotRequestDTO;
import com.edu.university.modules.schedule.dto.response.TimeSlotResponseDTO;

import java.util.List;
import java.util.UUID;

public interface TimeSlotService {
    TimeSlotResponseDTO create(TimeSlotRequestDTO requestDTO);
    List<TimeSlotResponseDTO> getAll();
    TimeSlotResponseDTO getById(UUID id);
    TimeSlotResponseDTO update(UUID id, TimeSlotRequestDTO requestDTO);
    void delete(UUID id);
}
