package com.edu.university.modules.academic.service;

import com.edu.university.modules.academic.dto.request.SemesterRequestDTO;
import com.edu.university.modules.academic.dto.response.SemesterResponseDTO;

import java.util.List;
import java.util.UUID;

public interface SemesterService {
    SemesterResponseDTO create(SemesterRequestDTO requestDTO);
    List<SemesterResponseDTO> getAll();
    SemesterResponseDTO getById(UUID id);
    SemesterResponseDTO update(UUID id, SemesterRequestDTO requestDTO);
    void delete(UUID id);
}
