package com.edu.university.modules.examination.service;

import com.edu.university.modules.examination.dto.request.ExamRegistrationRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamRegistrationResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ExamRegistrationService {
    ExamRegistrationResponseDTO create(ExamRegistrationRequestDTO requestDTO);
    List<ExamRegistrationResponseDTO> getByExamId(UUID examId);
    List<ExamRegistrationResponseDTO> getByStudentId(UUID studentId);
    void delete(UUID id);
}
