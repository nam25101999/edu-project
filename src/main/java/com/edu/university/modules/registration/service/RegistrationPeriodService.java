package com.edu.university.modules.registration.service;

import com.edu.university.modules.registration.dto.request.RegistrationPeriodRequestDTO;
import com.edu.university.modules.registration.dto.response.RegistrationPeriodResponseDTO;

import java.util.List;
import java.util.UUID;

public interface RegistrationPeriodService {
    RegistrationPeriodResponseDTO create(RegistrationPeriodRequestDTO requestDTO);
    List<RegistrationPeriodResponseDTO> getAll();
    RegistrationPeriodResponseDTO getById(UUID id);
    RegistrationPeriodResponseDTO update(UUID id, RegistrationPeriodRequestDTO requestDTO);
    void delete(UUID id);
}
