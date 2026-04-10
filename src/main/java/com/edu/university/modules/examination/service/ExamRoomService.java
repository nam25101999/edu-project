package com.edu.university.modules.examination.service;

import com.edu.university.modules.examination.dto.request.ExamRoomRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamRoomResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ExamRoomService {
    ExamRoomResponseDTO create(ExamRoomRequestDTO requestDTO);
    Page<ExamRoomResponseDTO> getByExamId(UUID examId, Pageable pageable);
    void delete(UUID id);
}
