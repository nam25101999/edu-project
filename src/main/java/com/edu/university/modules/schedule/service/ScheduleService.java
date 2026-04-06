package com.edu.university.modules.schedule.service;

import com.edu.university.modules.schedule.dto.request.ScheduleRequestDTO;
import com.edu.university.modules.schedule.dto.response.ScheduleResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ScheduleService {
    ScheduleResponseDTO create(ScheduleRequestDTO requestDTO);
    Page<ScheduleResponseDTO> getAll(Pageable pageable);
    ScheduleResponseDTO getById(UUID id);
    ScheduleResponseDTO update(UUID id, ScheduleRequestDTO requestDTO);
    void delete(UUID id);
}
