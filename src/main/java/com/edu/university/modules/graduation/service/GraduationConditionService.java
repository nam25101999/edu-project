package com.edu.university.modules.graduation.service;

import com.edu.university.modules.graduation.dto.request.GraduationConditionRequestDTO;
import com.edu.university.modules.graduation.dto.response.GraduationConditionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface GraduationConditionService {
    GraduationConditionResponseDTO create(GraduationConditionRequestDTO requestDTO);
    List<GraduationConditionResponseDTO> getAll();
    GraduationConditionResponseDTO getById(UUID id);
    GraduationConditionResponseDTO update(UUID id, GraduationConditionRequestDTO requestDTO);
    void delete(UUID id);
}
