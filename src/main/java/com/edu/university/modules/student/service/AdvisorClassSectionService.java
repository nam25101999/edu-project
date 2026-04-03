package com.edu.university.modules.student.service;

import com.edu.university.modules.student.dto.request.AdvisorClassSectionRequestDTO;
import com.edu.university.modules.student.dto.response.AdvisorClassSectionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface AdvisorClassSectionService {
    AdvisorClassSectionResponseDTO assignAdvisorToClass(AdvisorClassSectionRequestDTO requestDTO);
    List<AdvisorClassSectionResponseDTO> getAll();
    List<AdvisorClassSectionResponseDTO> getByAdvisorId(UUID advisorId);
    List<AdvisorClassSectionResponseDTO> getByClassId(UUID studentClassesId);
    AdvisorClassSectionResponseDTO update(UUID id, AdvisorClassSectionRequestDTO requestDTO);
    void delete(UUID id);
}