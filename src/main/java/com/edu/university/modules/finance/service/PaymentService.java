package com.edu.university.modules.finance.service;

import com.edu.university.modules.finance.dto.request.PaymentRequestDTO;
import com.edu.university.modules.finance.dto.response.PaymentResponseDTO;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentResponseDTO create(PaymentRequestDTO requestDTO);
    List<PaymentResponseDTO> getByStudentTuitionId(UUID studentTuitionId);
    void delete(UUID id);
}
