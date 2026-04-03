package com.edu.university.modules.schedule.service;

import com.edu.university.modules.schedule.dto.request.ScheduleRequestDTO;
import com.edu.university.modules.schedule.dto.response.ScheduleResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ScheduleService {
    ScheduleResponseDTO create(ScheduleRequestDTO requestDTO);
    List<ScheduleResponseDTO> getAll();
    ScheduleResponseDTO getById(UUID id);
    ScheduleResponseDTO update(UUID id, ScheduleRequestDTO requestDTO);
    void delete(UUID id);
}
