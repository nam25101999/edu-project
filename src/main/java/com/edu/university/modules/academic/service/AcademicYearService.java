package com.edu.university.modules.academic.service;

import com.edu.university.modules.academic.dto.request.AcademicYearRequestDTO;
import com.edu.university.modules.academic.dto.response.AcademicYearResponseDTO;

import java.util.List;
import java.util.UUID;

public interface AcademicYearService {
    AcademicYearResponseDTO create(AcademicYearRequestDTO requestDTO);
    List<AcademicYearResponseDTO> getAll();
    AcademicYearResponseDTO getById(UUID id);
    AcademicYearResponseDTO update(UUID id, AcademicYearRequestDTO requestDTO);
    void delete(UUID id);
}
