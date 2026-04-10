package com.edu.university.modules.finance.service;

import com.edu.university.modules.finance.dto.request.PaymentRequestDTO;
import com.edu.university.modules.finance.dto.response.PaymentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentService {
    PaymentResponseDTO create(PaymentRequestDTO requestDTO);
    Page<PaymentResponseDTO> getByStudentTuitionId(UUID studentTuitionId, Pageable pageable);
    void delete(UUID id);
}
