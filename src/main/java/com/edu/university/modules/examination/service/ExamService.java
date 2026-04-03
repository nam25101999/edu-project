package com.edu.university.modules.examination.service;

import com.edu.university.modules.examination.dto.request.ExamRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ExamService {
    ExamResponseDTO create(ExamRequestDTO requestDTO);
    List<ExamResponseDTO> getAll();
    ExamResponseDTO getById(UUID id);
    ExamResponseDTO update(UUID id, ExamRequestDTO requestDTO);
    void delete(UUID id);
}
