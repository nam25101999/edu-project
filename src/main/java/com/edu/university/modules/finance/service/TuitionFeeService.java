package com.edu.university.modules.finance.service;

import com.edu.university.modules.finance.dto.request.TuitionFeeRequestDTO;
import com.edu.university.modules.finance.dto.response.TuitionFeeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface TuitionFeeService {
    TuitionFeeResponseDTO create(TuitionFeeRequestDTO requestDTO);
    Page<TuitionFeeResponseDTO> getAll(Pageable pageable);
    TuitionFeeResponseDTO getById(UUID id);
    TuitionFeeResponseDTO update(UUID id, TuitionFeeRequestDTO requestDTO);
    void delete(UUID id);
}
