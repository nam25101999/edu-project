package com.edu.university.modules.examination.service;

import com.edu.university.modules.examination.dto.request.ExamTypeRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamTypeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ExamTypeService {
    ExamTypeResponseDTO create(ExamTypeRequestDTO requestDTO);
    Page<ExamTypeResponseDTO> getAll(Pageable pageable);
    ExamTypeResponseDTO getById(UUID id);
    ExamTypeResponseDTO update(UUID id, ExamTypeRequestDTO requestDTO);
    void delete(UUID id);
}
