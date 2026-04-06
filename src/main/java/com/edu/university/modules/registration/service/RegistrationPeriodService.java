package com.edu.university.modules.registration.service;

import com.edu.university.modules.registration.dto.request.RegistrationPeriodRequestDTO;
import com.edu.university.modules.registration.dto.response.RegistrationPeriodResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RegistrationPeriodService {
    RegistrationPeriodResponseDTO create(RegistrationPeriodRequestDTO requestDTO);
    Page<RegistrationPeriodResponseDTO> getAll(Pageable pageable);
    RegistrationPeriodResponseDTO getById(UUID id);
    RegistrationPeriodResponseDTO update(UUID id, RegistrationPeriodRequestDTO requestDTO);
    void delete(UUID id);
}
