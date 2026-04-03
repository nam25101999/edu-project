package com.edu.university.modules.curriculum.service;

import com.edu.university.modules.curriculum.dto.request.CourseRequestDTO;
import com.edu.university.modules.curriculum.dto.response.CourseResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CourseService {
    CourseResponseDTO create(CourseRequestDTO requestDTO);
    List<CourseResponseDTO> getAll();
    CourseResponseDTO getById(UUID id);
    CourseResponseDTO update(UUID id, CourseRequestDTO requestDTO);
    void delete(UUID id);
}
