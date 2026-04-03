package com.edu.university.modules.examination.service;

import com.edu.university.modules.examination.dto.request.ExamTypeRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamTypeResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ExamTypeService {
    ExamTypeResponseDTO create(ExamTypeRequestDTO requestDTO);
    List<ExamTypeResponseDTO> getAll();
    ExamTypeResponseDTO getById(UUID id);
    ExamTypeResponseDTO update(UUID id, ExamTypeRequestDTO requestDTO);
    void delete(UUID id);
}
