package com.edu.university.modules.graduation.service;

import com.edu.university.modules.graduation.dto.request.GraduationResultRequestDTO;
import com.edu.university.modules.graduation.dto.response.GraduationResultResponseDTO;

import java.util.List;
import java.util.UUID;

public interface GraduationResultService {
    GraduationResultResponseDTO create(GraduationResultRequestDTO requestDTO);
    List<GraduationResultResponseDTO> getByStudentId(UUID studentId);
    void delete(UUID id);
}
