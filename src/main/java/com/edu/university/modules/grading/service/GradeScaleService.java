package com.edu.university.modules.grading.service;

import com.edu.university.modules.grading.dto.request.GradeScaleRequestDTO;
import com.edu.university.modules.grading.dto.response.GradeScaleResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GradeScaleService {
    GradeScaleResponseDTO create(GradeScaleRequestDTO requestDTO);
    Page<GradeScaleResponseDTO> getAll(Pageable pageable);
    GradeScaleResponseDTO getById(UUID id);
    GradeScaleResponseDTO update(UUID id, GradeScaleRequestDTO requestDTO);
    void delete(UUID id);
}
