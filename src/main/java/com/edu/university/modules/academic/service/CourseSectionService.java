package com.edu.university.modules.academic.service;

import com.edu.university.modules.academic.dto.request.CourseSectionRequestDTO;
import com.edu.university.modules.academic.dto.response.CourseSectionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CourseSectionService {
    CourseSectionResponseDTO create(CourseSectionRequestDTO requestDTO);
    List<CourseSectionResponseDTO> getAll();
    CourseSectionResponseDTO getById(UUID id);
    CourseSectionResponseDTO update(UUID id, CourseSectionRequestDTO requestDTO);
    void delete(UUID id);
}
