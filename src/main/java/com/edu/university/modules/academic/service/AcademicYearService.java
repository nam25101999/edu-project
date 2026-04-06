package com.edu.university.modules.academic.service;

import com.edu.university.modules.academic.dto.request.AcademicYearRequestDTO;
import com.edu.university.modules.academic.dto.response.AcademicYearResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AcademicYearService {
    AcademicYearResponseDTO create(AcademicYearRequestDTO requestDTO);
    Page<AcademicYearResponseDTO> getAll(Pageable pageable);
    AcademicYearResponseDTO getById(UUID id);
    AcademicYearResponseDTO update(UUID id, AcademicYearRequestDTO requestDTO);
    void delete(UUID id);
}
