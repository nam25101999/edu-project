package com.edu.university.modules.examination.service;

import com.edu.university.modules.examination.dto.request.ExamRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ExamService {
    ExamResponseDTO create(ExamRequestDTO requestDTO);
    Page<ExamResponseDTO> getAll(Pageable pageable);
    ExamResponseDTO getById(UUID id);
    ExamResponseDTO update(UUID id, ExamRequestDTO requestDTO);
    void delete(UUID id);
}
