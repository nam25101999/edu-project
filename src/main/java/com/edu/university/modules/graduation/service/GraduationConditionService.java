package com.edu.university.modules.graduation.service;

import com.edu.university.modules.graduation.dto.request.GraduationConditionRequestDTO;
import com.edu.university.modules.graduation.dto.response.GraduationConditionResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GraduationConditionService {
    GraduationConditionResponseDTO create(GraduationConditionRequestDTO requestDTO);
    Page<GraduationConditionResponseDTO> getAll(Pageable pageable);
    GraduationConditionResponseDTO getById(UUID id);
    GraduationConditionResponseDTO update(UUID id, GraduationConditionRequestDTO requestDTO);
    void delete(UUID id);
}
