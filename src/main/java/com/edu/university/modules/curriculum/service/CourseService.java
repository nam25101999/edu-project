package com.edu.university.modules.curriculum.service;

import com.edu.university.modules.curriculum.dto.request.CourseRequestDTO;
import com.edu.university.modules.curriculum.dto.response.CourseResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CourseService {
    CourseResponseDTO create(CourseRequestDTO requestDTO);
    Page<CourseResponseDTO> getAll(String search, Pageable pageable);
    CourseResponseDTO getById(UUID id);
    CourseResponseDTO update(UUID id, CourseRequestDTO requestDTO);
    void delete(UUID id);
}
