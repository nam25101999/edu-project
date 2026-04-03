package com.edu.university.modules.curriculum.service;

import com.edu.university.modules.curriculum.dto.request.CoursePrerequisiteRequestDTO;
import com.edu.university.modules.curriculum.dto.response.CoursePrerequisiteResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CoursePrerequisiteService {
    CoursePrerequisiteResponseDTO create(CoursePrerequisiteRequestDTO requestDTO);
    List<CoursePrerequisiteResponseDTO> getAll();
    CoursePrerequisiteResponseDTO getById(UUID id);
    void delete(UUID id);
}
