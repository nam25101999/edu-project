package com.edu.university.modules.examination.service;

import com.edu.university.modules.examination.dto.request.ExamRegistrationRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamRegistrationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ExamRegistrationService {
    ExamRegistrationResponseDTO create(ExamRegistrationRequestDTO requestDTO);
    Page<ExamRegistrationResponseDTO> getByExamId(UUID examId, Pageable pageable);
    Page<ExamRegistrationResponseDTO> getByStudentId(UUID studentId, Pageable pageable);
    void delete(UUID id);
}
