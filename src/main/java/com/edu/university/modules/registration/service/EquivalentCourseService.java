package com.edu.university.modules.registration.service;

import com.edu.university.modules.registration.dto.request.EquivalentCourseRequestDTO;
import com.edu.university.modules.registration.dto.response.EquivalentCourseResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EquivalentCourseService {
    EquivalentCourseResponseDTO create(EquivalentCourseRequestDTO requestDTO);
    Page<EquivalentCourseResponseDTO> getAll(Pageable pageable);
    EquivalentCourseResponseDTO getById(UUID id);
    void delete(UUID id);
}
