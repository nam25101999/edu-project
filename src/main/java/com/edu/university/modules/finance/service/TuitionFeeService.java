package com.edu.university.modules.finance.service;

import com.edu.university.modules.finance.dto.request.TuitionFeeRequestDTO;
import com.edu.university.modules.finance.dto.response.TuitionFeeResponseDTO;

import java.util.List;
import java.util.UUID;

public interface TuitionFeeService {
    TuitionFeeResponseDTO create(TuitionFeeRequestDTO requestDTO);
    List<TuitionFeeResponseDTO> getAll();
    TuitionFeeResponseDTO getById(UUID id);
    TuitionFeeResponseDTO update(UUID id, TuitionFeeRequestDTO requestDTO);
    void delete(UUID id);
}
