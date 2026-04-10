package com.edu.university.modules.academic.service;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.modules.academic.dto.request.CourseSectionRequestDTO;
import com.edu.university.modules.academic.dto.response.CourseSectionResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CourseSectionService {
    CourseSectionResponseDTO create(CourseSectionRequestDTO requestDTO);
    PageResponse<CourseSectionResponseDTO> getAll(UUID departmentId, UUID majorId, Pageable pageable);
    CourseSectionResponseDTO getById(UUID id);
    CourseSectionResponseDTO update(UUID id, CourseSectionRequestDTO requestDTO);
    void delete(UUID id);
}
