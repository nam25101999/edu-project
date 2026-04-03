package com.edu.university.modules.grading.service;

import com.edu.university.modules.grading.dto.request.GradeScaleRequestDTO;
import com.edu.university.modules.grading.dto.response.GradeScaleResponseDTO;

import java.util.List;
import java.util.UUID;

public interface GradeScaleService {
    GradeScaleResponseDTO create(GradeScaleRequestDTO requestDTO);
    List<GradeScaleResponseDTO> getAll();
    GradeScaleResponseDTO getById(UUID id);
    GradeScaleResponseDTO update(UUID id, GradeScaleRequestDTO requestDTO);
    void delete(UUID id);
}
