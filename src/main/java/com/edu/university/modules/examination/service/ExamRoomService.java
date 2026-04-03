package com.edu.university.modules.examination.service;

import com.edu.university.modules.examination.dto.request.ExamRoomRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamRoomResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ExamRoomService {
    ExamRoomResponseDTO create(ExamRoomRequestDTO requestDTO);
    List<ExamRoomResponseDTO> getByExamId(UUID examId);
    void delete(UUID id);
}
