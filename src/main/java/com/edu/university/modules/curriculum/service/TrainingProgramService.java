package com.edu.university.modules.curriculum.service;

import com.edu.university.modules.curriculum.dto.request.TrainingProgramRequestDTO;
import com.edu.university.modules.curriculum.dto.response.TrainingProgramResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TrainingProgramService {
    TrainingProgramResponseDTO create(TrainingProgramRequestDTO requestDTO);
    Page<TrainingProgramResponseDTO> getAll(Pageable pageable);
    TrainingProgramResponseDTO getById(UUID id);
    TrainingProgramResponseDTO update(UUID id, TrainingProgramRequestDTO requestDTO);
    void delete(UUID id);
}
