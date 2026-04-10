package com.edu.university.modules.curriculum.service;

import com.edu.university.modules.curriculum.dto.request.TrainingProgramCourseRequestDTO;
import com.edu.university.modules.curriculum.dto.response.TrainingProgramCourseResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TrainingProgramCourseService {
    TrainingProgramCourseResponseDTO create(TrainingProgramCourseRequestDTO requestDTO);
    Page<TrainingProgramCourseResponseDTO> getAll(UUID trainingProgramId, Pageable pageable);
    TrainingProgramCourseResponseDTO getById(UUID id);
    TrainingProgramCourseResponseDTO update(UUID id, TrainingProgramCourseRequestDTO requestDTO);
    void delete(UUID id);
}
