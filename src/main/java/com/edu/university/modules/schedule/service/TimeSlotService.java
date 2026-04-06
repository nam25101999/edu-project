package com.edu.university.modules.schedule.service;

import com.edu.university.modules.schedule.dto.request.TimeSlotRequestDTO;
import com.edu.university.modules.schedule.dto.response.TimeSlotResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TimeSlotService {
    TimeSlotResponseDTO create(TimeSlotRequestDTO requestDTO);
    Page<TimeSlotResponseDTO> getAll(Pageable pageable);
    TimeSlotResponseDTO getById(UUID id);
    TimeSlotResponseDTO update(UUID id, TimeSlotRequestDTO requestDTO);
    void delete(UUID id);
}
