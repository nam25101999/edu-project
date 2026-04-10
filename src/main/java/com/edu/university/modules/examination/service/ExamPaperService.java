package com.edu.university.modules.examination.service;

import com.edu.university.modules.examination.dto.request.ExamPaperRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamPaperResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ExamPaperService {
    ExamPaperResponseDTO create(ExamPaperRequestDTO requestDTO);
    Page<ExamPaperResponseDTO> getByExamId(UUID examId, Pageable pageable);
    void delete(UUID id);
}
