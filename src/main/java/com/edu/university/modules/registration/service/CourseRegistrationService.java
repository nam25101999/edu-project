package com.edu.university.modules.registration.service;

import com.edu.university.modules.registration.dto.request.CourseRegistrationRequestDTO;
import com.edu.university.modules.registration.dto.request.EligibilityCheckRequest;
import com.edu.university.modules.registration.dto.response.CourseRegistrationResponseDTO;
import com.edu.university.modules.registration.dto.response.EligibilityCheckResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CourseRegistrationService {
    CourseRegistrationResponseDTO create(CourseRegistrationRequestDTO requestDTO);
    Page<CourseRegistrationResponseDTO> getAll(Pageable pageable);
    CourseRegistrationResponseDTO getById(UUID id);
    CourseRegistrationResponseDTO update(UUID id, CourseRegistrationRequestDTO requestDTO);
    void delete(UUID id);
    EligibilityCheckResponse checkEligibility(EligibilityCheckRequest request);
}
