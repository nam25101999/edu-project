package com.edu.university.modules.grading.service;

import com.edu.university.modules.grading.dto.request.GradeComponentRequestDTO;
import com.edu.university.modules.grading.dto.response.GradeComponentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GradeComponentService {
    GradeComponentResponseDTO create(GradeComponentRequestDTO requestDTO);
    Page<GradeComponentResponseDTO> getByCourseSectionId(UUID courseSectionId, Pageable pageable);
    GradeComponentResponseDTO update(UUID id, GradeComponentRequestDTO requestDTO);
    void delete(UUID id);
}
