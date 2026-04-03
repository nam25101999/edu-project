package com.edu.university.modules.grading.service;

import com.edu.university.modules.grading.dto.request.GradeComponentRequestDTO;
import com.edu.university.modules.grading.dto.response.GradeComponentResponseDTO;

import java.util.List;
import java.util.UUID;

public interface GradeComponentService {
    GradeComponentResponseDTO create(GradeComponentRequestDTO requestDTO);
    List<GradeComponentResponseDTO> getByCourseSectionId(UUID courseSectionId);
    GradeComponentResponseDTO update(UUID id, GradeComponentRequestDTO requestDTO);
    void delete(UUID id);
}
