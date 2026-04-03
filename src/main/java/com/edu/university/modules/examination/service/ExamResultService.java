package com.edu.university.modules.examination.service;

import com.edu.university.modules.examination.dto.request.ExamResultRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamResultResponseDTO;

import java.util.UUID;

public interface ExamResultService {
    ExamResultResponseDTO upsert(ExamResultRequestDTO requestDTO);
    ExamResultResponseDTO getByRegistrationId(UUID registrationId);
    void delete(UUID id);
}
