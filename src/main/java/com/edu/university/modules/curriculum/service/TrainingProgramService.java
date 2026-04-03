package com.edu.university.modules.curriculum.service;

import com.edu.university.modules.curriculum.dto.request.TrainingProgramRequestDTO;
import com.edu.university.modules.curriculum.dto.response.TrainingProgramResponseDTO;

import java.util.List;
import java.util.UUID;

public interface TrainingProgramService {
    TrainingProgramResponseDTO create(TrainingProgramRequestDTO requestDTO);
    List<TrainingProgramResponseDTO> getAll();
    TrainingProgramResponseDTO getById(UUID id);
    TrainingProgramResponseDTO update(UUID id, TrainingProgramRequestDTO requestDTO);
    void delete(UUID id);
}
