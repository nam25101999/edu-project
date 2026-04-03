package com.edu.university.modules.examination.service;

import com.edu.university.modules.examination.dto.request.ExamPaperRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamPaperResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ExamPaperService {
    ExamPaperResponseDTO create(ExamPaperRequestDTO requestDTO);
    List<ExamPaperResponseDTO> getByExamId(UUID examId);
    void delete(UUID id);
}
