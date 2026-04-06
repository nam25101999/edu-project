package com.edu.university.modules.academic.service;

import com.edu.university.modules.academic.dto.request.SemesterRequestDTO;
import com.edu.university.modules.academic.dto.response.SemesterResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SemesterService {
    SemesterResponseDTO create(SemesterRequestDTO requestDTO);
    Page<SemesterResponseDTO> getAll(Pageable pageable);
    SemesterResponseDTO getById(UUID id);
    SemesterResponseDTO update(UUID id, SemesterRequestDTO requestDTO);
    void delete(UUID id);
}
