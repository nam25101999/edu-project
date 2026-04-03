package com.edu.university.modules.registration.service;

import com.edu.university.modules.registration.dto.request.CourseRegistrationRequestDTO;
import com.edu.university.modules.registration.dto.response.CourseRegistrationResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CourseRegistrationService {
    CourseRegistrationResponseDTO create(CourseRegistrationRequestDTO requestDTO);
    List<CourseRegistrationResponseDTO> getAll();
    CourseRegistrationResponseDTO getById(UUID id);
    CourseRegistrationResponseDTO update(UUID id, CourseRegistrationRequestDTO requestDTO);
    void delete(UUID id);
}
