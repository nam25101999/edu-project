package com.edu.university.modules.graduation.service;

import com.edu.university.modules.graduation.dto.request.GraduationResultRequestDTO;
import com.edu.university.modules.graduation.dto.response.GraduationResultResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GraduationResultService {
    GraduationResultResponseDTO create(GraduationResultRequestDTO requestDTO);
    Page<GraduationResultResponseDTO> getByStudentId(UUID studentId, Pageable pageable);
    void delete(UUID id);
}
