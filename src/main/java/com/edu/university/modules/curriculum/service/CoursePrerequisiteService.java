package com.edu.university.modules.curriculum.service;

import com.edu.university.modules.curriculum.dto.request.CoursePrerequisiteRequestDTO;
import com.edu.university.modules.curriculum.dto.response.CoursePrerequisiteResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CoursePrerequisiteService {
    CoursePrerequisiteResponseDTO create(CoursePrerequisiteRequestDTO requestDTO);
    Page<CoursePrerequisiteResponseDTO> getAll(Pageable pageable);
    CoursePrerequisiteResponseDTO getById(UUID id);
    void delete(UUID id);
}
