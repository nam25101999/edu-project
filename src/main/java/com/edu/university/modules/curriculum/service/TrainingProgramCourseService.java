package com.edu.university.modules.curriculum.service;

import com.edu.university.modules.curriculum.dto.request.TrainingProgramCourseRequestDTO;
import com.edu.university.modules.curriculum.dto.response.TrainingProgramCourseResponseDTO;

import java.util.List;
import java.util.UUID;

public interface TrainingProgramCourseService {
    TrainingProgramCourseResponseDTO create(TrainingProgramCourseRequestDTO requestDTO);
    List<TrainingProgramCourseResponseDTO> getAll();
    TrainingProgramCourseResponseDTO getById(UUID id);
    TrainingProgramCourseResponseDTO update(UUID id, TrainingProgramCourseRequestDTO requestDTO);
    void delete(UUID id);
}
