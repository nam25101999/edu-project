package com.edu.university.modules.registration.service;

import com.edu.university.modules.registration.dto.request.EquivalentCourseRequestDTO;
import com.edu.university.modules.registration.dto.response.EquivalentCourseResponseDTO;

import java.util.List;
import java.util.UUID;

public interface EquivalentCourseService {
    EquivalentCourseResponseDTO create(EquivalentCourseRequestDTO requestDTO);
    List<EquivalentCourseResponseDTO> getAll();
    EquivalentCourseResponseDTO getById(UUID id);
    void delete(UUID id);
}
